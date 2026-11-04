package com.blinkit.servlet;

import com.blinkit.dao.ProductDAO;
import com.blinkit.model.CartItem;
import com.blinkit.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    @SuppressWarnings("unchecked")
    private Map<Integer,CartItem> cart(HttpServletRequest req) {
        HttpSession session=req.getSession();
        Map<Integer,CartItem> cart=(Map<Integer,CartItem>)session.getAttribute("cart");
        if (cart==null) { cart=new LinkedHashMap<>(); session.setAttribute("cart",cart); }
        return cart;
    }

    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        req.getRequestDispatcher("/cart.jsp").forward(req,resp);
    }

    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        req.setCharacterEncoding("UTF-8");
        Map<Integer,CartItem> cart=cart(req);
        String action=req.getParameter("action");
        try {
            if ("add".equals(action)) {
                int id=Integer.parseInt(req.getParameter("productId"));
                Product p=new ProductDAO().findById(id);
                if (p==null) { resp.sendError(404,"Product not found."); return; }
                if (p.getStock()<1) req.getSession().setAttribute("flash","This product is out of stock.");
                else {
                    CartItem item=cart.get(id);
                    if (item==null) cart.put(id,new CartItem(id,p.getName(),p.getPrice(),p.getImageUrl(),1));
                    else if (item.getQuantity()<p.getStock()) item.setQuantity(item.getQuantity()+1);
                    req.getSession().setAttribute("flash",p.getName()+" added to your cart.");
                }
            } else if ("update".equals(action)) {
                int id=Integer.parseInt(req.getParameter("productId"));
                int quantity=Integer.parseInt(req.getParameter("quantity"));
                CartItem item=cart.get(id);
                if (item!=null) {
                    if (quantity<=0) cart.remove(id);
                    else {
                        Product p=new ProductDAO().findById(id);
                        if (p==null || p.getStock()<1) cart.remove(id);
                        else item.setQuantity(Math.min(quantity,p.getStock()));
                    }
                }
            } else if ("remove".equals(action)) {
                cart.remove(Integer.parseInt(req.getParameter("productId")));
            } else if ("clear".equals(action)) cart.clear();
            resp.sendRedirect(req.getContextPath()+"/cart");
        } catch (NumberFormatException e) {
            resp.sendError(400,"Invalid product ID or quantity.");
        } catch (SQLException e) {
            throw new ServletException("Cart operation failed.",e);
        }
    }
}
