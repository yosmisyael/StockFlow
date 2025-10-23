package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManagerRepository {
    /**
     * Registers a new manager in the database.
     * Note: This method currently stores the password as plain text.
     * Consider implementing password hashing (e.g., using BCrypt) for security.
     *
     * @param name     The full name of the manager.
     * @param email    The unique email address for the manager.
     * @param company  The company the manager is associated with (can be null or empty).
     * @param password The manager's plain-text password. **(Security Warning: Hashing recommended)**
     * @return {@code true} if the manager was registered successfully (at least one row inserted), {@code false} otherwise.
     */
    public boolean registerManager(String name, String email, String company, String password) {
        String sql = "INSERT INTO managers (name, email, company, password) VALUES (?, ?, ?, ?)";

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
