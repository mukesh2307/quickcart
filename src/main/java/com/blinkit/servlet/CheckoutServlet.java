package com.blinkit.servlet;

import com.blinkit.dao.OrderDAO;
import com.blinkit.model.CartItem;
import com.blinkit.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CheckoutServlet.class.getName());

    @SuppressWarnings("unchecked")
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        Map<Integer,CartItem> cart=(Map<Integer,CartItem>)req.getSession().getAttribute("cart");
        if (cart==null || cart.isEmpty()) { resp.sendRedirect(req.getContextPath()+"/cart"); return; }
        req.getRequestDispatcher("/checkout.jsp").forward(req,resp);
    }

    @SuppressWarnings("unchecked")
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        req.setCharacterEncoding("UTF-8");
        String name=req.getParameter("customerName"), address=req.getParameter("address");
        name=name==null ? null : name.trim();
        address=address==null ? null : address.trim();
        if (name==null || name.isBlank() || address==null || address.isBlank()
                || name.length()>120 || address.length()>500) {
            req.setAttribute("error","Enter your name (max 120 characters) and address (max 500 characters).");
            doGet(req,resp); return;
        }
        HttpSession session=req.getSession(false);
        User user=session==null ? null : (User)session.getAttribute("loggedInUser");
        if (user==null) { resp.sendRedirect(req.getContextPath()+"/login"); return; }
        Map<Integer,CartItem> cart=(Map<Integer,CartItem>)session.getAttribute("cart");
        if (cart==null || cart.isEmpty()) { resp.sendRedirect(req.getContextPath()+"/cart"); return; }
        try {
            long id=new OrderDAO().placeOrder(name,address,cart);
            cart.clear();
            session.setAttribute("lastOrderId",id);
            resp.sendRedirect(req.getContextPath()+"/order-success");
        } catch (OrderDAO.OutOfStockException | IllegalArgumentException e) {
            req.setAttribute("error",e.getMessage());
            doGet(req,resp);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Unable to place customer order.",e);
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            req.setAttribute("error","Your order could not be placed right now. Please try again later.");
            doGet(req,resp);
        }
    }
}
