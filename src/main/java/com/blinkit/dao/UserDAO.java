package com.blinkit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.blinkit.model.User;
import com.blinkit.util.CustomerPassword;
import com.blinkit.util.DBConnection;

public class UserDAO {

    public void registerUser(User user, char[] password) throws SQLException {
        if (user == null || !CustomerPassword.isValidForRegistration(password)) {
            throw new IllegalArgumentException("Invalid registration data.");
        }
        String passwordHash = CustomerPassword.hash(password);
        String sql = "INSERT INTO users (full_name, email, phone, delivery_address, password_hash) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getDeliveryAddress());
            ps.setString(5, passwordHash);
            ps.executeUpdate();
        }
    }

    public User loginUser(String email, char[] password) throws SQLException {
        if (email == null || email.isBlank() || email.length() > 255 || !CustomerPassword.canVerify(password)) {
            return null;
        }
        String sql = "SELECT id,full_name,email,phone,delivery_address,password_hash FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase(java.util.Locale.ROOT));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !CustomerPassword.verify(password, rs.getString("password_hash"))) return null;
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setDeliveryAddress(rs.getString("delivery_address"));
                return user;
            }
        }
    }
}