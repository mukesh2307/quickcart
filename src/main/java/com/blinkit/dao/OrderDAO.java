package com.blinkit.dao;

import com.blinkit.model.CartItem;
import com.blinkit.util.DBConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Map;

public class OrderDAO {
    public long placeOrder(String customerName, String address, Map<Integer, CartItem> cart)
            throws SQLException, OutOfStockException {
        if (cart == null || cart.isEmpty()) throw new IllegalArgumentException("Your cart is empty.");
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                BigDecimal total = BigDecimal.ZERO;
                for (CartItem item : cart.values()) {
                    try (PreparedStatement ps=con.prepareStatement(
                            "SELECT name,price,stock FROM products WHERE id=? FOR UPDATE")) {
                        ps.setInt(1,item.getProductId());
                        try (ResultSet rs=ps.executeQuery()) {
                            if (!rs.next()) throw new OutOfStockException("A product is no longer available.");
                            int stock=rs.getInt("stock");
                            if (item.getQuantity()>stock)
                                throw new OutOfStockException("Not enough stock for "+rs.getString("name")+". Available: "+stock);
                            total=total.add(rs.getBigDecimal("price").multiply(BigDecimal.valueOf(item.getQuantity())));
                        }
                    }
                }
                long orderId;
                try (PreparedStatement ps=con.prepareStatement(
                        "INSERT INTO orders(customer_name,delivery_address,total_amount,status) VALUES(?,?,?,'PLACED')",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1,customerName.trim()); ps.setString(2,address.trim()); ps.setBigDecimal(3,total);
                    ps.executeUpdate();
                    try (ResultSet keys=ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Could not create order.");
                        orderId=keys.getLong(1);
                    }
                }
                for (CartItem item : cart.values()) {
                    String name; BigDecimal price;
                    try (PreparedStatement ps=con.prepareStatement("SELECT name,price FROM products WHERE id=?")) {
                        ps.setInt(1,item.getProductId());
                        try (ResultSet rs=ps.executeQuery()) {
                            if (!rs.next()) throw new SQLException("Product no longer exists.");
                            name=rs.getString("name"); price=rs.getBigDecimal("price");
                        }
                    }
                    try (PreparedStatement ps=con.prepareStatement(
                            "INSERT INTO order_items(order_id,product_id,product_name,quantity,unit_price) VALUES(?,?,?,?,?)")) {
                        ps.setLong(1,orderId); ps.setInt(2,item.getProductId()); ps.setString(3,name);
                        ps.setInt(4,item.getQuantity()); ps.setBigDecimal(5,price); ps.executeUpdate();
                    }
                    try (PreparedStatement ps=con.prepareStatement(
                            "UPDATE products SET stock=stock-? WHERE id=? AND stock>=?")) {
                        ps.setInt(1,item.getQuantity()); ps.setInt(2,item.getProductId()); ps.setInt(3,item.getQuantity());
                        if (ps.executeUpdate()!=1) throw new OutOfStockException("Stock changed. Please try checkout again.");
                    }
                }
                con.commit();
                return orderId;
            } catch (SQLException | RuntimeException | OutOfStockException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public static class OutOfStockException extends Exception {
        public OutOfStockException(String message) { super(message); }
    }
}
