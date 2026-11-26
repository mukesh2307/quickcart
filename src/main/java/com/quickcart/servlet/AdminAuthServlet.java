package com.quickcart.servlet;

import com.quickcart.dao.AdminDAO;
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
        req.getRequestDispatcher("/admin-login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        try {
            if (adminDAO.authenticate(email, password)) {
                req.getSession();
                req.changeSessionId();
                req.getSession().setAttribute("adminEmail", email.trim().toLowerCase(Locale.ROOT));
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                return;
            }
            req.setAttribute("error", "Invalid admin email or password.");
            req.setAttribute("email", email);
        } catch (SQLException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "Unable to authenticate administrator.", e);
            req.setAttribute("error", "Admin login is temporarily unavailable. Please try again later.");
        }
        req.getRequestDispatcher("/admin-login.jsp").forward(req, resp);
    }
}
