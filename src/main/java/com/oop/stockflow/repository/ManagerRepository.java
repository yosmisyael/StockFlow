package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManagerRepository {
    public boolean registerManager(String name, String email, String company, String password) {
        String sql = "INSERT INTO manager (name, email, company, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, company);
            stmt.setString(4, password);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to register manager: " + e.getMessage());
            return false;
        }
    }
}
