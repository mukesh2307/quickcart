package com.quickcart.dao;

import com.quickcart.util.AdminPassword;
import com.quickcart.util.DBConnection;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;

public class AdminDAO {
    public boolean authenticate(String email, String password) throws SQLException, GeneralSecurityException {
        if (email == null || password == null || email.isBlank() || password.isEmpty()
                || email.length() > 255 || password.length() > 1024) return false;
        String sql = "SELECT password_hash FROM admin_users WHERE email=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.trim().toLowerCase(Locale.ROOT));
            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) return false;
                char[] passwordChars = password.toCharArray();
                try {
                    return AdminPassword.verify(passwordChars, results.getString("password_hash"));
                } finally {
                    Arrays.fill(passwordChars, '\0');
                }
            }
        }
    }
}
