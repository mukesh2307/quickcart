package com.blinkit.servlet;

import com.blinkit.dao.ProductDAO;
import com.blinkit.model.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private final ProductDAO dao = new ProductDAO();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String search=req.getParameter("q"), category=req.getParameter("category");
            if (search!=null && search.length()>100) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Search text is too long.");
                return;
            }
            var categories=dao.findCategories();
            if (category!=null && !category.isBlank() && !"all".equalsIgnoreCase(category)
                    && !categories.contains(category)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Unknown product category.");
                return;
            }
            req.setAttribute("products",dao.findProducts(search,category));
            req.setAttribute("categories",categories);
            req.setAttribute("search",search==null?"":search);
            req.setAttribute("selectedCategory",category==null?"all":category);
            req.setAttribute("cartItemCount",getCartItemCount(req));
            req.getRequestDispatcher("/products.jsp").forward(req,resp);
        } catch (SQLException e) {
            throw new ServletException("Unable to load products. Check XAMPP and the database import.",e);
        }
    }

    @SuppressWarnings("unchecked")
    private int getCartItemCount(HttpServletRequest req) {
        HttpSession session=req.getSession(false);
        if (session==null) return 0;
        Map<Integer,CartItem> cart=(Map<Integer,CartItem>)session.getAttribute("cart");
        if (cart==null) return 0;
        return cart.values().stream().mapToInt(CartItem::getQuantity).sum();
    }
}
