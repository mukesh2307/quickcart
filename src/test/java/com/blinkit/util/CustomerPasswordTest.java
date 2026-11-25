package com.blinkit.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class CustomerPasswordTest {
    @Test
    void hashesAndVerifiesPasswordsWithoutStoringThePlaintext() {
        char[] password = "a-secure-password-123".toCharArray();
        try {
            String hash = CustomerPassword.hash(password);

            assertNotEquals(new String(password), hash);
            assertTrue(CustomerPassword.verify(password, hash));
            assertFalse(CustomerPassword.verify("a-different-password".toCharArray(), hash));
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    @Test
    void enforcesRegistrationLengthAndBcryptByteLimit() {
        assertFalse(CustomerPassword.isValidForRegistration("short-pass"));
        assertTrue(CustomerPassword.isValidForRegistration("long-enough-pass"));
        assertFalse(CustomerPassword.isValidForRegistration("é".repeat(37)));
    }
}
