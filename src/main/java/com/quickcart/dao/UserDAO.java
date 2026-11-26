
package com.quickcart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import com.quickcart.model.User;
import com.quickcart.util.DBConnection;

public class UserDAO {

    // Register a new user with a BCrypt-hashed password
    public void registerUser(User user) throws SQLException {

        String sql = "INSERT INTO users " +
                "(full_name, email, phone, delivery_address, password_hash) " +
                "VALUES (?, ?, ?, ?, ?)";

        String hashedPassword = BCrypt.hashpw(
                user.getPassword(),
                BCrypt.gensalt(12));

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getDeliveryAddress());
            ps.setString(5, hashedPassword);

            ps.executeUpdate();
        }
    }

    // Authenticate user using BCrypt
    public User loginUser(String email, String password) {

        String sql = "SELECT id, full_name, email, phone, " +
                "delivery_address, password_hash " +
                "FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String storedHash = rs.getString("password_hash");

                    if (storedHash == null ||
                            !BCrypt.checkpw(password, storedHash)) {
                        return null;
                    }

                    User user = new User();

                    user.setId(rs.getInt("id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setDeliveryAddress(
                            rs.getString("delivery_address"));

                    return user;
                }
            }

        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }
}
