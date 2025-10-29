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

/**
 * Repository class for handling authentication-related database operations.
 * Implements singleton pattern to ensure only one instance manages authentication data.
 * Provides methods for user login, session management, and password validation using BCrypt.
 */
public class AuthRepository {
    private static AuthRepository instance;

    /**
     * Private constructor to prevent direct instantiation.
     * Ensures only one instance can be created through getInstance().
     */
    private AuthRepository() {}

    /**
     * Returns the singleton instance of the AuthRepository.
     * Creates the instance on the first call (lazy initialization).
     * Note: This simple lazy initialization is not thread-safe.
     * Consider eager initialization for better thread safety if needed.
     *
     * @return The singleton AuthRepository instance.
     */
    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    /**
     * Attempts to authenticate a user (Manager or Staff) based on email and password.
     * It first checks the managers table, then the staff table.
     *
     * @param email    The user's email address.
     * @param password The user's plain-text password.
     * @return An {@link AuthenticatedUser} object containing the user's ID, name, and type upon successful authentication,
     * or {@code null} if authentication fails (invalid email, wrong password, or database error).
     */
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
     * Helper method to validate the plain-text password against the hashed password
     * retrieved from the database and create an AuthenticatedUser object if valid.
     *
     * @param rs            The ResultSet positioned at the row containing user data (id, name, password).
     * @param plainPassword The plain-text password entered by the user.
     * @param userType      The type of user (MANAGER or STAFF).
     * @return An {@link AuthenticatedUser} object if the password is valid, otherwise {@code null}.
     * @throws SQLException If an error occurs reading from the ResultSet.
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
     * Creates a new session record linking the user ID, user type, and generated token.
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
     * Deletes a session from the database based on the user ID associated with the session.
     * Typically called during logout.
     *
     * @param userId The ID of the user whose session should be deleted.
     * @return true if the session was successfully deleted (one or more rows affected), false otherwise (e.g., no session found or a database error occurred).
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
