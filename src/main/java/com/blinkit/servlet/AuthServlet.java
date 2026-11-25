package com.blinkit.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.blinkit.dao.UserDAO;
import com.blinkit.filter.CustomerCsrfFilter;
import com.blinkit.model.User;
import com.blinkit.util.CsrfTokens;
import com.blinkit.util.CustomerPassword;
import com.blinkit.util.LoginRateLimiter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/logout".equals(path)) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        req.setAttribute("mode", "/register".equals(path) ? "register" : "login");
        showLogin(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            logout(req, resp);
        } else if ("/register".equals(path)) {
            String fullName = trim(req.getParameter("fullName"));
            String email = normalizeEmail(req.getParameter("email"));
            String phone = trim(req.getParameter("phone"));
            String address = trim(req.getParameter("deliveryAddress"));
            String password = req.getParameter("password");
            String confirmation = req.getParameter("confirmPassword");

            if (!validRegistration(fullName, email, phone, address, password, confirmation)) {
                req.setAttribute("mode", "register");
                req.setAttribute("error", "Check your details. Passwords must match and contain at least 12 characters.");
                showLogin(req, resp);
                return;
            }

            char[] passwordChars = password.toCharArray();
            try {
                userDAO.registerUser(new User(fullName, email, phone, address), passwordChars);
                req.setAttribute("mode", "login");
                req.setAttribute("success", "Account created successfully! Please login.");
            } catch (SQLException e) {
                req.setAttribute("mode", "register");
                if (e.getErrorCode() == 1062) {
                    req.setAttribute("error", "Unable to create an account with those details.");
                } else {
                    LOGGER.log(Level.SEVERE, "Unable to register user.", e);
                    resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    req.setAttribute("error", "Account creation is temporarily unavailable. Please try again later.");
                }
            } finally {
                Arrays.fill(passwordChars, '\0');
            }
            showLogin(req, resp);

        } else if ("/login".equals(path)) {
            authenticateCustomer(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void authenticateCustomer(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = normalizeEmail(req.getParameter("email"));
        String passwordValue = req.getParameter("password");
        char[] password = passwordValue == null ? new char[0] : passwordValue.toCharArray();
        try {
            LoginRateLimiter limiter = LoginRateLimiter.shared();
            int retryAfter = limiter.retryAfterSeconds(req.getRemoteAddr());
            if (retryAfter > 0) {
                resp.setStatus(429);
                resp.setHeader("Retry-After", Integer.toString(retryAfter));
                req.setAttribute("mode", "login");
                req.setAttribute("error", "Too many sign-in attempts. Please try again later.");
                showLogin(req, resp);
                return;
            }

            User user = validEmail(email) && CustomerPassword.canVerify(password)
                    ? userDAO.loginUser(email, password)
                    : null;
            if (user == null) {
                limiter.recordFailure(req.getRemoteAddr());
                req.setAttribute("mode", "login");
                req.setAttribute("error", "Invalid email or password.");
                showLogin(req, resp);
                return;
            }

            limiter.recordSuccess(req.getRemoteAddr());
            HttpSession session = req.getSession();
            req.changeSessionId();
            CsrfTokens.rotate(session, CustomerCsrfFilter.TOKEN_ATTRIBUTE);
            session.setAttribute("loggedInUser", user);
            resp.sendRedirect(req.getContextPath() + "/products");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to authenticate customer.", e);
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            req.setAttribute("mode", "login");
            req.setAttribute("error", "Sign-in is temporarily unavailable. Please try again later.");
            showLogin(req, resp);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private boolean validRegistration(String fullName, String email, String phone, String address,
                                      String password, String confirmation) {
        return fullName != null && !fullName.isBlank() && fullName.length() <= 120
                && validEmail(email)
                && phone != null && phone.matches("[0-9]{10}")
                && address != null && !address.isBlank() && address.length() <= 500
                && CustomerPassword.isValidForRegistration(password)
                && password.equals(confirmation);
    }

    private boolean validEmail(String email) {
        return email != null && email.length() <= 255 && EMAIL_PATTERN.matcher(email).matches();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private void showLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}