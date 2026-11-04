package com.blinkit.servlet;

import com.blinkit.dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private final ProductDAO dao = new ProductDAO();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String search=req.getParameter("q"), category=req.getParameter("category");
            req.setAttribute("products",dao.findProducts(search,category));
            req.setAttribute("categories",dao.findCategories());
            req.setAttribute("search",search==null?"":search);
            req.setAttribute("selectedCategory",category==null?"all":category);
            req.getRequestDispatcher("/products.jsp").forward(req,resp);
        } catch (SQLException e) {
            throw new ServletException("Unable to load products. Check XAMPP and the database import.",e);
        }
    }
}
