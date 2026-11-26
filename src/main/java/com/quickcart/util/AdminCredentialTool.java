package com.quickcart.util;

import java.io.Console;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public final class AdminCredentialTool {
    private AdminCredentialTool() {}

    public static void main(String[] args) throws GeneralSecurityException {
        Console console = System.console();
        if (console == null) {
            System.err.println("Run this command from a terminal that supports hidden password input.");
            System.exit(1);
        }

        char[] password = console.readPassword("New admin password (at least 12 characters): ");
        char[] confirmation = console.readPassword("Confirm admin password: ");
        if (password == null || password.length < 12 || password.length > 1024
                || !Arrays.equals(password, confirmation)) {
            if (password != null) Arrays.fill(password, '\0');
            if (confirmation != null) Arrays.fill(confirmation, '\0');
            System.err.println("Passwords must match and contain between 12 and 1024 characters.");
            System.exit(1);
            return;
        }

        try {
            System.out.println(AdminPassword.hash(password));
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');
        }
    }
}
