package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRepository {
    private static ProductRepository instance;
    private ProductRepository() {}

    public static ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    public String getProductNameBySku(int sku) {
        String sql = "SELECT name FROM products WHERE sku = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sku);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
        return null;
    }

    public String getProductBrandBySku(int sku) {
        String sql = "SELECT brand FROM products WHERE sku = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sku);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("brand");
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
        return null;
    }


}
