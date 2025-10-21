package com.oop.stockflow.db;

import java.sql.Connection;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {
    private static HikariDataSource dataSource;

    static {
        try {
            Dotenv dotenv = Dotenv.load();

            String dbUrl = dotenv.get("DB_URL");
            String user = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            config.setUsername(user);
            config.setPassword(password);

            config.setMaximumPoolSize(10);
            config.setIdleTimeout(30000);
            config.setLeakDetectionThreshold(20000);

            dataSource = new HikariDataSource(config);

            System.out.println("[INFO] Connection pool initialized.");

        } catch (Exception e) {
            System.out.println("[ERROR] Unable to connect to database:  " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeDataSource() throws SQLException {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("[INFO] Connection pool closed.");
        }
    }
}
