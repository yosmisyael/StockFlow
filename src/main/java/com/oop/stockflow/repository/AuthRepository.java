package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.TransactionType;
import com.oop.stockflow.model.UserType;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthRepository {
    private Connection conn;

    public AuthRepository() {
        try {
            conn =  DatabaseManager.getConnection();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public AuthenticatedUser login(String email, String password) {
        String managerQuery = "SELECT id, name, password FROM managers WHERE email = ?";
        try (PreparedStatement managerStmt = conn.prepareStatement(managerQuery)) {
            managerStmt.setString(1, email);
            ResultSet managerResult = managerStmt.executeQuery();

            if (managerResult.next()) {
                return validateAndCreateUser(managerResult, password, UserType.MANAGER);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed querying for manager: " + e.getMessage());
            return null;
        }

        String staffQuery = "SELECT id, name, password FROM staff WHERE email = ?";
        try (PreparedStatement staffStmt = conn.prepareStatement(staffQuery)) {
            staffStmt.setString(1, email);
            ResultSet staffResult = staffStmt.executeQuery();

            if (staffResult.next()) {
                return validateAndCreateUser(staffResult, password, UserType.STAFF);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed querying for staff: " + e.getMessage());
            return null;
        }

        return null;
    }

    /**
     * Helper method to reduce code duplication.
     * Checks password and creates an AuthenticatedUser if valid.
     */
    private AuthenticatedUser validateAndCreateUser(ResultSet rs, String plainPassword, UserType userType) throws SQLException {
        String hashedPassword = rs.getString("password");

        if (BCrypt.checkpw(plainPassword, hashedPassword)) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            return new AuthenticatedUser(id, name, userType);
        }

        return null;
    }

    private void saveSession(long userId, TransactionType userType, String token) throws SQLException {
        String query = "INSERT INTO sessions (user_id, user_type, token) VALUES (?, ?::user_role, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setLong(1, userId);
        stmt.setString(2, userType.getDbValue());
        stmt.setString(3, token);
        stmt.executeUpdate();
    }
}
