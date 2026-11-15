<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("loggedInUser") != null) {
        response.sendRedirect(request.getContextPath() + "/products");
    } else {
        response.sendRedirect(request.getContextPath() + "/login");
    }
%>