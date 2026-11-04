package com.blinkit.servlet;

import com.blinkit.dao.OrderDAO;
import com.blinkit.model.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
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
        if (name==null || name.isBlank() || address==null || address.isBlank()
                || name.length()>120 || address.length()>500) {
            req.setAttribute("error","Enter your name (max 120 characters) and address (max 500 characters).");
            doGet(req,resp); return;
        }
        Map<Integer,CartItem> cart=(Map<Integer,CartItem>)req.getSession().getAttribute("cart");
        if (cart==null || cart.isEmpty()) { resp.sendRedirect(req.getContextPath()+"/cart"); return; }
        try {
            long id=new OrderDAO().placeOrder(name,address,cart);
            cart.clear();
            req.getSession().setAttribute("lastOrderId",id);
            resp.sendRedirect(req.getContextPath()+"/order-success");
        } catch (OrderDAO.OutOfStockException | IllegalArgumentException e) {
            req.setAttribute("error",e.getMessage()); doGet(req,resp);
        } catch (Exception e) {
            throw new ServletException("Could not place order. Check the server log and database.",e);
        }
    }
}
