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
import java.util.UUID;

public class AuthRepository {
    private static AuthRepository instance;
    private AuthRepository() {}

    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public AuthenticatedUser login(String email, String password) {
        String managerQuery = "SELECT id, name, password FROM managers WHERE email = ?";
        try (PreparedStatement managerStmt = DatabaseManager.getConnection().prepareStatement(managerQuery)) {
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
        try (PreparedStatement staffStmt = DatabaseManager.getConnection().prepareStatement(staffQuery)) {
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

    /**
     * Generates a unique session token and saves the session details to the database.
     *
     * @param userId   The ID of the user (Manager or Staff).
     * @param userType The type of the user (MANAGER or STAFF).
     * @return The generated session token if successful, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public boolean saveSession(long userId, UserType userType) { // Changed return type
        String token = UUID.randomUUID().toString(); // Generate token here
        String query = "INSERT INTO sessions (user_id, user_type, token) VALUES (?, ?::user_role, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setString(2, userType.getDbValue());
            stmt.setString(3, token);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to save session: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a session from the database based on the session token.
     * Typically called during logout.
     *
     * @param userId The session token to delete.
     * @return true if the session was successfully deleted, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean deleteSession(long userId) { // Parameter changed to long userId
        String query = "DELETE FROM sessions WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            // executeUpdate returns the number of rows affected
            return stmt.executeUpdate() > 0; // Returns true if at least one session was deleted
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to delete session for user ID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
