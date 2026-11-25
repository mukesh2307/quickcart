package com.blinkit.servlet;

import com.blinkit.dao.OrderDAO;
import com.blinkit.dao.ProductDAO;
import com.blinkit.model.OrderSummary;
import com.blinkit.model.Product;
import com.blinkit.util.CsrfTokens;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ensureCsrfToken(req.getSession());
        String path = req.getPathInfo();
        try {
            if ("/dashboard".equals(path)) {
                List<Product> products = productDAO.findProducts(null, "all");
                long lowStockCount = products.stream().filter(product -> product.getStock() <= 5).count();
                req.setAttribute("productCount", products.size());
                req.setAttribute("lowStockCount", lowStockCount);
                List<OrderSummary> recentOrders = orderDAO.findOrders(8);
                req.setAttribute("recentOrderCount", recentOrders.size());
                req.setAttribute("recentOrders", recentOrders);
                req.getRequestDispatcher("/admin-dashboard.jsp").forward(req, resp);
            } else if ("/inventory".equals(path)) {
                req.setAttribute("products", productDAO.findProducts(null, "all"));
                req.getRequestDispatcher("/admin-inventory.jsp").forward(req, resp);
            } else if ("/orders".equals(path)) {
                req.setAttribute("orders", orderDAO.findOrders(50));
                req.getRequestDispatcher("/admin-orders.jsp").forward(req, resp);
            } else if ("/order-history".equals(path)) {
                showOrderHistory(req, resp);
            } else if ("/popular-items".equals(path)) {
                req.setAttribute("popularProducts", orderDAO.findPopularProducts(100));
                req.getRequestDispatcher("/admin-popular-items.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException("Unable to load the admin page.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!CsrfTokens.matches(req.getSession(false), "adminCsrfToken", req.getParameter("csrfToken"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid form token.");
            return;
        }

        String path = req.getPathInfo();
        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }
        if (!"/inventory".equals(path)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            int productId = Integer.parseInt(req.getParameter("productId"));
            int stock = Integer.parseInt(req.getParameter("stock"));
            if (productId < 1 || stock < 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID and stock must be valid non-negative values.");
                return;
            }
            if (!productDAO.updateStock(productId, stock)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found.");
                return;
            }
            req.getSession().setAttribute("adminFlash", "Inventory updated.");
            resp.sendRedirect(req.getContextPath() + "/admin/inventory");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID and stock must be whole numbers.");
        } catch (SQLException e) {
            throw new ServletException("Unable to update inventory.", e);
        }
    }

    private void showOrderHistory(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        req.setAttribute("orders", orderDAO.findOrderHistory());
        String requestedId = req.getParameter("orderId");
        if (requestedId != null && !requestedId.isBlank()) {
            try {
                long orderId = Long.parseLong(requestedId);
                if (orderId < 1) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID must be positive.");
                    return;
                }
                OrderSummary order = orderDAO.findOrder(orderId);
                if (order == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                    return;
                }
                req.setAttribute("selectedOrder", order);
                req.setAttribute("orderLines", orderDAO.findOrderLines(orderId));
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID must be a whole number.");
                return;
            }
        }
        req.getRequestDispatcher("/admin-order-history.jsp").forward(req, resp);
    }

    private void ensureCsrfToken(HttpSession session) {
        CsrfTokens.ensure(session, "adminCsrfToken");
    }
}
