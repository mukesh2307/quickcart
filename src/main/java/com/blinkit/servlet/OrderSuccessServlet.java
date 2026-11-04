package com.blinkit.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/order-success")
public class OrderSuccessServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        Object id=req.getSession().getAttribute("lastOrderId");
        if (id==null) { resp.sendRedirect(req.getContextPath()+"/products"); return; }
        req.setAttribute("orderId",id);
        req.getRequestDispatcher("/order-success.jsp").forward(req,resp);
    }
}
