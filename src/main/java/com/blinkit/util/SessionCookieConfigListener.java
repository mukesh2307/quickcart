package com.blinkit.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SessionCookieConfigListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        LoginRateLimiter.shared();
        SessionCookieConfig cookie = event.getServletContext().getSessionCookieConfig();
        cookie.setHttpOnly(true);

        String configured = System.getenv("SESSION_COOKIE_SECURE");
        if (configured == null || configured.isBlank()) return;
        if (!"true".equalsIgnoreCase(configured) && !"false".equalsIgnoreCase(configured)) {
            throw new IllegalStateException("SESSION_COOKIE_SECURE must be true or false.");
        }
        cookie.setSecure(Boolean.parseBoolean(configured));
    }
}
