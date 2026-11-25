package com.blinkit.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.http.HttpSession;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CsrfTokensTest {
    @Test
    void createsAndRotatesSessionBoundTokens() {
        Map<String, Object> attributes = new HashMap<>();
        HttpSession session = (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(),
                new Class<?>[] {HttpSession.class},
                (proxy, method, arguments) -> {
                    if ("getAttribute".equals(method.getName())) {
                        return attributes.get(arguments[0]);
                    }
                    if ("setAttribute".equals(method.getName())) {
                        attributes.put((String) arguments[0], arguments[1]);
                        return null;
                    }
                    return null;
                });

        String first = CsrfTokens.ensure(session, "token");
        assertNotNull(first);
        assertTrue(CsrfTokens.matches(session, "token", first));
        assertFalse(CsrfTokens.matches(session, "token", "incorrect"));
        assertFalse(CsrfTokens.matches(null, "token", first));

        String rotated = CsrfTokens.rotate(session, "token");
        assertNotEquals(first, rotated);
        assertFalse(CsrfTokens.matches(session, "token", first));
        assertTrue(CsrfTokens.matches(session, "token", rotated));
    }
}
