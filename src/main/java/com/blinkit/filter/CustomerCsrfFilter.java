package com.blinkit.filter;

import com.blinkit.util.CsrfTokens;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/login", "/register", "/logout", "/products", "/cart", "/checkout"})
public class CustomerCsrfFilter implements Filter {
    public static final String TOKEN_ATTRIBUTE = "customerCsrfToken";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if ("GET".equalsIgnoreCase(req.getMethod())) {
            HttpSession session = req.getSession();
            CsrfTokens.ensure(session, TOKEN_ATTRIBUTE);
        } else if ("POST".equalsIgnoreCase(req.getMethod())
                && !CsrfTokens.matches(req.getSession(false), TOKEN_ATTRIBUTE, req.getParameter("csrfToken"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid form token.");
            return;
        }

        chain.doFilter(request, response);
    }
}
