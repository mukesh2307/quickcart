package com.blinkit.dao;

import com.blinkit.model.Product;
import com.blinkit.util.DBConnection;
import java.sql.*;
import java.util.*;

public class ProductDAO {
    public List<Product> findProducts(String search, String category) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT p.id,p.category_id,c.name category_name,p.name,p.description,p.price,p.stock,p.image_url " +
            "FROM products p JOIN categories c ON p.category_id=c.id WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            sql.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
            args.add("%" + search.trim() + "%"); args.add("%" + search.trim() + "%");
        }
        if (category != null && !category.isBlank() && !"all".equalsIgnoreCase(category)) {
            sql.append(" AND c.name=?"); args.add(category);
        }
        sql.append(" ORDER BY c.name,p.name");
        List<Product> out = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i=0; i<args.size(); i++) ps.setObject(i+1, args.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    public Product findById(int id) throws SQLException {
        String sql = "SELECT p.id,p.category_id,c.name category_name,p.name,p.description,p.price,p.stock,p.image_url " +
                     "FROM products p JOIN categories c ON p.category_id=c.id WHERE p.id=?";
        try (Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setInt(1,id);
            try (ResultSet rs=ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<String> findCategories() throws SQLException {
        List<String> list = new ArrayList<>();
        try (Connection c=DBConnection.getConnection();
             PreparedStatement ps=c.prepareStatement("SELECT name FROM categories ORDER BY name");
             ResultSet rs=ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        }
        return list;
    }

    private Product map(ResultSet rs) throws SQLException {
        return new Product(rs.getInt("id"), rs.getInt("category_id"), rs.getString("category_name"),
            rs.getString("name"), rs.getString("description"), rs.getBigDecimal("price"),
            rs.getInt("stock"), rs.getString("image_url"));
    }
}
