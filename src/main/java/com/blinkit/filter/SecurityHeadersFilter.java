package com.blinkit.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class SecurityHeadersFilter implements Filter {

    private static final String CONTENT_SECURITY_POLICY = "default-src 'self'; "
            + "img-src 'self' data: https://images.pexels.com https://placehold.co; "
            + "script-src 'self' https://unpkg.com; "
            + "style-src 'self' https://cdn.jsdelivr.net; "
            + "font-src 'self' data: https://cdn.jsdelivr.net; "
            + "object-src 'none'; "
            + "base-uri 'self'; "
            + "frame-ancestors 'none'; "
            + "form-action 'self'";

    private boolean hstsEnabled;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String configured = System.getenv("HSTS_ENABLED");
        if (configured == null || configured.isBlank())
            return;
        if (!"true".equalsIgnoreCase(configured) && !"false".equalsIgnoreCase(configured)) {
            throw new ServletException("HSTS_ENABLED must be true or false.");
        }
        hstsEnabled = Boolean.parseBoolean(configured);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Content-Security-Policy", CONTENT_SECURITY_POLICY);
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");
        resp.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        String path = req.getServletPath();
        if (!path.startsWith("/css/") && !path.startsWith("/js/") && !path.startsWith("/images/")) {
            resp.setHeader("Cache-Control", "no-store");
            resp.setHeader("Pragma", "no-cache");
        }
        if (hstsEnabled && req.isSecure()) {
            resp.setHeader("Strict-Transport-Security", "max-age=31536000");
        }
        chain.doFilter(request, response);
    }
}
