package com.blinkit.util;

import jakarta.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class CsrfTokens {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;

    private CsrfTokens() {}

    public static String ensure(HttpSession session, String attributeName) {
        Object existing = session.getAttribute(attributeName);
        if (existing instanceof String token) return token;
        return rotate(session, attributeName);
    }

    public static String rotate(HttpSession session, String attributeName) {
        byte[] token = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(token);
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(token);
        session.setAttribute(attributeName, encoded);
        return encoded;
    }

    public static boolean matches(HttpSession session, String attributeName, String supplied) {
        if (session == null || supplied == null) return false;
        Object expected = session.getAttribute(attributeName);
        return expected instanceof String token
                && MessageDigest.isEqual(token.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        supplied.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
