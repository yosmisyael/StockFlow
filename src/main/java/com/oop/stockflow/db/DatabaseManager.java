package com.oop.stockflow.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseManager {
    private static Connection connection = null;

    static {
        try {
            Dotenv dotenv = Dotenv.load();

            String dbUrl = dotenv.get("DB_URL");
            String user = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            connection = DriverManager.getConnection(dbUrl, user, password);

            System.out.println("[INFO] Connected to database.");
        } catch (SQLException e) {
            System.out.println("[ERROR] Unable to connect to database:  " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
