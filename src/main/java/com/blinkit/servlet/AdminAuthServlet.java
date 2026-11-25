package com.blinkit.servlet;

import com.blinkit.dao.AdminDAO;
import com.blinkit.util.CsrfTokens;
import com.blinkit.util.LoginRateLimiter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/login")
public class AdminAuthServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminAuthServlet.class.getName());
    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) != null && req.getSession(false).getAttribute("adminEmail") != null) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }
        CsrfTokens.ensure(req.getSession(), "adminCsrfToken");
        req.getRequestDispatcher("/admin-login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        if (!CsrfTokens.matches(req.getSession(false), "adminCsrfToken", req.getParameter("csrfToken"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid form token.");
            return;
        }

        LoginRateLimiter limiter = LoginRateLimiter.shared();
        int retryAfter = limiter.retryAfterSeconds(req.getRemoteAddr());
        if (retryAfter > 0) {
            resp.setStatus(429);
            resp.setHeader("Retry-After", Integer.toString(retryAfter));
            req.setAttribute("error", "Too many sign-in attempts. Please try again later.");
            req.getRequestDispatcher("/admin-login.jsp").forward(req, resp);
            return;
        }

        try {
            if (adminDAO.authenticate(email, password)) {
                limiter.recordSuccess(req.getRemoteAddr());
                req.getSession();
                req.changeSessionId();
                req.getSession().setAttribute("adminEmail", email.trim().toLowerCase(Locale.ROOT));
                CsrfTokens.rotate(req.getSession(), "adminCsrfToken");
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                return;
            }
            limiter.recordFailure(req.getRemoteAddr());
            req.setAttribute("error", "Invalid admin email or password.");
            req.setAttribute("email", email);
        } catch (SQLException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "Unable to authenticate administrator.", e);
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            req.setAttribute("error", "Admin login is temporarily unavailable. Please try again later.");
        }
        req.getRequestDispatcher("/admin-login.jsp").forward(req, resp);
    }
}
