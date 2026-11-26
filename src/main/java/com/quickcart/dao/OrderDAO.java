package com.quickcart.dao;

import com.quickcart.model.CartItem;
import com.quickcart.model.OrderLine;
import com.quickcart.model.OrderSummary;
import com.quickcart.model.PopularProduct;
import com.quickcart.util.DBConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDAO {
    public List<OrderSummary> findOrders(int limit) throws SQLException {
        String sql = "SELECT id,customer_name,delivery_address,total_amount,status,created_at " +
                "FROM orders ORDER BY created_at DESC,id DESC LIMIT ?";
        List<OrderSummary> orders = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) orders.add(mapOrder(results));
            }
        }
        return orders;
    }

    public List<OrderSummary> findOrderHistory() throws SQLException {
        String sql = "SELECT id,customer_name,delivery_address,total_amount,status,created_at " +
                "FROM orders ORDER BY created_at DESC,id DESC";
        List<OrderSummary> orders = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) orders.add(mapOrder(results));
        }
        return orders;
    }

    public OrderSummary findOrder(long orderId) throws SQLException {
        String sql = "SELECT id,customer_name,delivery_address,total_amount,status,created_at FROM orders WHERE id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? mapOrder(results) : null;
            }
        }
    }

    public List<OrderLine> findOrderLines(long orderId) throws SQLException {
        String sql = "SELECT product_name,quantity,unit_price FROM order_items WHERE order_id=? ORDER BY id";
        List<OrderLine> lines = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    lines.add(new OrderLine(results.getString("product_name"),
                            results.getInt("quantity"), results.getBigDecimal("unit_price")));
                }
            }
        }
        return lines;
    }

    public List<PopularProduct> findPopularProducts(int limit) throws SQLException {
        String sql = "SELECT p.id,p.name,c.name AS category_name,p.price,p.stock," +
                "SUM(oi.quantity) AS quantity_sold,COUNT(DISTINCT oi.order_id) AS order_count " +
                "FROM order_items oi JOIN orders o ON o.id=oi.order_id " +
                "JOIN products p ON p.id=oi.product_id JOIN categories c ON c.id=p.category_id " +
                "WHERE o.status='PLACED' GROUP BY p.id,p.name,c.name,p.price,p.stock " +
                "ORDER BY quantity_sold DESC,order_count DESC,p.name ASC LIMIT ?";
        List<PopularProduct> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    products.add(new PopularProduct(results.getInt("id"), results.getString("name"),
                            results.getString("category_name"), results.getBigDecimal("price"),
                            results.getInt("stock"), results.getLong("quantity_sold"),
                            results.getLong("order_count")));
                }
            }
        }
        return products;
    }

    private OrderSummary mapOrder(ResultSet results) throws SQLException {
        return new OrderSummary(results.getLong("id"), results.getString("customer_name"),
                results.getString("delivery_address"), results.getBigDecimal("total_amount"),
                results.getString("status"), results.getTimestamp("created_at"));
    }

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
