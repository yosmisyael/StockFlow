package com.oop.stockflow.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/course_stockflow?user=postgres&password=root";
    private static Connection connection = null;

    private DatabaseManager() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL);
                System.out.println("[INFO] Connected to database.");
            } catch (SQLException e) {
                System.out.println("[INFO] Failed to connect to database: " + e.getMessage());
            }
        }
        return connection;
    }
}
