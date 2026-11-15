package com.blinkit.servlet;

import java.io.IOException;

import com.blinkit.dao.UserDAO;
import com.blinkit.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("mode", "/register".equals(path) ? "register" : "login");
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/register".equals(path)) {
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String address = req.getParameter("deliveryAddress");
            String password = req.getParameter("password");
            String confirmPassword = req.getParameter("confirmPassword");

            if (!password.equals(confirmPassword)) {
                req.setAttribute("mode", "register");
                req.setAttribute("error", "Passwords do not match!");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            User newUser = new User(fullName, email, phone, address, password);
            if (userDAO.registerUser(newUser)) {
                req.setAttribute("mode", "login");
                req.setAttribute("success", "Account created successfully! Please login.");
            } else {
                req.setAttribute("mode", "register");
                req.setAttribute("error", "Registration failed. Email may already exist.");
            }
            req.getRequestDispatcher("/login.jsp").forward(req, resp);

        } else if ("/login".equals(path)) {
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            User user = userDAO.loginUser(email, password);
            if (user != null) {
                HttpSession session = req.getSession();
                session.setAttribute("loggedInUser", user);
                resp.sendRedirect(req.getContextPath() + "/products");
            } else {
                req.setAttribute("mode", "login");
                req.setAttribute("error", "Invalid email or password.");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            }
        }
    }
}