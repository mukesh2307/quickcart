
package com.blinkit.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class DBConnection {
    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = requiredEnvironmentVariable("DB_URL");
        String user = requiredEnvironmentVariable("DB_USER");
        String password = requiredEnvironmentVariable("DB_PASSWORD");
        validateConnectionUrl(url);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver is unavailable.", e);
        }

        return DriverManager.getConnection(url, user, password);
    }

    private static String requiredEnvironmentVariable(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new SQLException("Required database configuration is missing: " + name + ".");
        }
        return value;
    }

    private static void validateConnectionUrl(String url) throws SQLException {
        if (!url.startsWith("jdbc:mysql://")) {
            throw new SQLException("DB_URL must be a MySQL JDBC URL.");
        }

        try {
            URI uri = URI.create(url.substring("jdbc:".length()));
            String host = uri.getHost();
            if (host == null || host.isBlank()) throw new IllegalArgumentException();
            if (uri.getRawUserInfo() != null
                    || hasQueryParameter(uri.getRawQuery(), "user")
                    || hasQueryParameter(uri.getRawQuery(), "password")) {
                throw new SQLException("DB_URL must not contain database credentials.");
            }
            String normalizedHost = host.toLowerCase(Locale.ROOT);
            boolean local = "localhost".equals(normalizedHost)
                    || "127.0.0.1".equals(normalizedHost)
                    || "::1".equals(normalizedHost)
                    || "[::1]".equals(normalizedHost);
            String sslMode = queryParameter(uri.getRawQuery(), "sslMode");
            if (!local && !"VERIFY_IDENTITY".equalsIgnoreCase(sslMode)) {
                throw new SQLException("Remote MySQL connections require sslMode=VERIFY_IDENTITY.");
            }
            if (hasQueryParameter(uri.getRawQuery(), "allowPublicKeyRetrieval", "true")) {
                throw new SQLException("allowPublicKeyRetrieval=true is not allowed.");
            }
        } catch (IllegalArgumentException e) {
            throw new SQLException("DB_URL is not a valid MySQL JDBC URL.", e);
        }
    }

    private static String queryParameter(String query, String name) {
        if (query == null) return null;
        for (String part : query.split("&")) {
            int separator = part.indexOf('=');
            if (separator > 0 && name.equalsIgnoreCase(decode(part.substring(0, separator)))) {
                return decode(part.substring(separator + 1));
            }
        }
        return null;
    }

    private static boolean hasQueryParameter(String query, String name) {
        if (query == null) return false;
        for (String part : query.split("&")) {
            int separator = part.indexOf('=');
            String parameterName = separator < 0 ? part : part.substring(0, separator);
            if (name.equalsIgnoreCase(decode(parameterName))) return true;
        }
        return false;
    }

    private static boolean hasQueryParameter(String query, String name, String value) {
        if (query == null) return false;
        for (String part : query.split("&")) {
            int separator = part.indexOf('=');
            if (separator > 0
                    && name.equalsIgnoreCase(decode(part.substring(0, separator)))
                    && value.equalsIgnoreCase(decode(part.substring(separator + 1)))) {
                return true;
            }
        }
        return false;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
