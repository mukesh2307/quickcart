package com.blinkit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class LoginRateLimiterTest {
    @Test
    void temporarilyBlocksRepeatedFailuresAndClearsAfterSuccess() {
        LoginRateLimiter limiter = LoginRateLimiter.shared();
        String address = "test-" + UUID.randomUUID();
        try {
            assertEquals(0, limiter.retryAfterSeconds(address));
            for (int attempt = 0; attempt < 20; attempt++) {
                limiter.recordFailure(address);
            }
            assertTrue(limiter.retryAfterSeconds(address) > 0);
        } finally {
            limiter.recordSuccess(address);
        }
        assertEquals(0, limiter.retryAfterSeconds(address));
    }
}
