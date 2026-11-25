package com.blinkit.servlet;

import com.blinkit.dao.ProductDAO;
import com.blinkit.model.CartItem;
import com.blinkit.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
                if (id<1) { resp.sendError(400,"Product ID must be positive."); return; }
                Product p=new ProductDAO().findById(id);
                if (p==null) { resp.sendError(404,"Product not found."); return; }
                if (p.getStock()<1) req.getSession().setAttribute("flash","This product is out of stock.");
                else {
                    CartItem item=cart.get(id);
                    if (item==null) {
                        cart.put(id,new CartItem(id,p.getName(),p.getPrice(),p.getImageUrl(),1));
                        req.getSession().setAttribute("flash",p.getName()+" added to your cart.");
                    } else if (item.getQuantity()<p.getStock()) {
                        item.setQuantity(item.getQuantity()+1);
                        req.getSession().setAttribute("flash",p.getName()+" added to your cart.");
                    } else {
                        req.getSession().setAttribute("flash","You've reached the available stock for "+p.getName()+".");
                    }
                }
            } else if ("addSelected".equals(action)) {
                String[] productIds=req.getParameterValues("productId");
                String[] quantities=req.getParameterValues("quantity");
                if (productIds==null || quantities==null || productIds.length==0 || productIds.length!=quantities.length) {
                    resp.sendError(400,"Invalid selected products or quantities.");
                    return;
                }

                ProductDAO dao=new ProductDAO();
                Map<Integer,Long> additions=new LinkedHashMap<>();
                for (int i=0;i<productIds.length;i++) {
                    int id=Integer.parseInt(productIds[i]);
                    int quantity=Integer.parseInt(quantities[i]);
                    if (id<1 || quantity<1) {
                        resp.sendError(400,"Selected quantities must be positive.");
                        return;
                    }
                    long totalQuantity=additions.getOrDefault(id,0L)+quantity;
                    if (totalQuantity>Integer.MAX_VALUE) {
                        resp.sendError(400,"Selected quantity is too large.");
                        return;
                    }
                    additions.put(id,totalQuantity);
                }

                Map<Integer,Product> selectedProducts=new LinkedHashMap<>();
                for (Map.Entry<Integer,Long> addition:additions.entrySet()) {
                    Product p=dao.findById(addition.getKey());
                    if (p==null) { resp.sendError(404,"Product not found."); return; }
                    int inCart=cart.containsKey(p.getId()) ? cart.get(p.getId()).getQuantity() : 0;
                    if (inCart+addition.getValue()>p.getStock()) {
                        req.getSession().setAttribute("flash","There is not enough stock to add the selected quantities.");
                        resp.sendRedirect(productsUrl(req));
                        return;
                    }
                    selectedProducts.put(p.getId(),p);
                }

                for (Map.Entry<Integer,Long> addition:additions.entrySet()) {
                    Product p=selectedProducts.get(addition.getKey());
                    CartItem item=cart.get(p.getId());
                    int quantity=addition.getValue().intValue();
                    if (item==null) cart.put(p.getId(),new CartItem(p.getId(),p.getName(),p.getPrice(),p.getImageUrl(),quantity));
                    else item.setQuantity(item.getQuantity()+quantity);
                }
                req.getSession().setAttribute("flash","Selected items added to your cart.");
            } else if ("update".equals(action)) {
                int id=Integer.parseInt(req.getParameter("productId"));
                int quantity=Integer.parseInt(req.getParameter("quantity"));
                if (id<1 || quantity<0) {
                    resp.sendError(400,"Product ID must be positive and quantity must not be negative.");
                    return;
                }
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
                int id=Integer.parseInt(req.getParameter("productId"));
                if (id<1) { resp.sendError(400,"Product ID must be positive."); return; }
                cart.remove(id);
            } else if ("clear".equals(action)) cart.clear();
            else {
                resp.sendError(400,"Unknown cart action.");
                return;
            }
            if ("add".equals(action) || "addSelected".equals(action)) resp.sendRedirect(productsUrl(req));
            else resp.sendRedirect(req.getContextPath()+"/cart");
        } catch (NumberFormatException e) {
            resp.sendError(400,"Invalid product ID or quantity.");
        } catch (SQLException e) {
            throw new ServletException("Cart operation failed.",e);
        }
    }

    private String productsUrl(HttpServletRequest req) {
        StringBuilder url=new StringBuilder(req.getContextPath()).append("/products");
        String search=req.getParameter("q");
        String category=req.getParameter("category");
        if (search!=null && !search.isBlank()) {
            url.append("?q=").append(URLEncoder.encode(search,StandardCharsets.UTF_8));
        }
        if (category!=null && !category.isBlank() && !"all".equalsIgnoreCase(category)) {
            url.append(url.indexOf("?")<0 ? "?" : "&")
               .append("category=").append(URLEncoder.encode(category,StandardCharsets.UTF_8));
        }
        return url.toString();
    }
}
