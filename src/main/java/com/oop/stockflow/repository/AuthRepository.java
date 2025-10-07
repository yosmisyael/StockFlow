package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;
import com.oop.stockflow.model.UserType;
import org.mindrot.jbcrypt.BCrypt;
import org.w3c.dom.html.HTMLStyleElement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthRepository {
    private Connection conn;

    public AuthRepository() {
        try {
            conn =  DatabaseManager.getConnection();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public boolean login(String email, String password) {
        String query = "SELECT id, password FROM manager WHERE email = ?";

        try {
            PreparedStatement prepareStatement = conn.prepareStatement(query);

            prepareStatement.setString(1, email);

            ResultSet result = prepareStatement.executeQuery();

            UserType userType = UserType.MANAGER;

            // perform checking in staff records
            if (!result.next()) {
                query = "SELECT user_id AS id, password FROM staff WHERE email = ?";
                prepareStatement = conn.prepareStatement(query);
                prepareStatement.setString(1, email);
                result = prepareStatement.executeQuery();
                userType = UserType.STAFF;
                System.out.println("staff login detected");
            }

            // validate password
            long userId = result.getLong("id");

            String hashedPassword = result.getString("password");

            boolean isPasswordValid = BCrypt.checkpw(password, hashedPassword);

            if (!isPasswordValid) {
                return false;
            }

            String token = UUID.randomUUID().toString();

            saveSession(userId, userType, token);

            return true;
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());

            return false;
        }
    }

    private void saveSession(long userId, UserType userType, String token) throws SQLException {
        String query = "INSERT INTO sessions (user_id, user_type, token) VALUES (?, ?::user_role, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setLong(1, userId);
        stmt.setString(2, userType.getDbValue());
        stmt.setString(3, token);
        stmt.executeUpdate();
    }
}
