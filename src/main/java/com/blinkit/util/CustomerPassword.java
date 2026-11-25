package com.blinkit.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.nio.charset.StandardCharsets;

public final class CustomerPassword {
    public static final int MIN_CHARACTERS = 12;
    public static final int MAX_UTF8_BYTES = 72;
    private static final int COST = configuredCost();

    private CustomerPassword() {}

    public static boolean isValidForRegistration(String password) {
        return password != null
                && password.codePointCount(0, password.length()) >= MIN_CHARACTERS
                && password.getBytes(StandardCharsets.UTF_8).length <= MAX_UTF8_BYTES;
    }

    public static boolean isValidForRegistration(char[] password) {
        return password != null && isValidForRegistration(new String(password));
    }

    public static boolean canVerify(char[] password) {
        return password != null && password.length > 0
                && new String(password).getBytes(StandardCharsets.UTF_8).length <= MAX_UTF8_BYTES;
    }

    public static String hash(char[] password) {
        if (!canVerify(password)) throw new IllegalArgumentException("Invalid password length.");
        return BCrypt.withDefaults().hashToString(COST, password);
    }

    public static boolean verify(char[] password, String encodedHash) {
        if (!canVerify(password) || encodedHash == null || encodedHash.isBlank()) return false;
        try {
            return BCrypt.verifyer().verify(password, encodedHash).verified;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static int configuredCost() {
        String value = System.getenv("BCRYPT_COST");
        if (value == null || value.isBlank()) return 12;
        try {
            int cost = Integer.parseInt(value);
            if (cost < 10 || cost > 16) throw new IllegalStateException("BCRYPT_COST must be between 10 and 16.");
            return cost;
        } catch (NumberFormatException e) {
            throw new IllegalStateException("BCRYPT_COST must be an integer between 10 and 16.", e);
        }
    }
}
