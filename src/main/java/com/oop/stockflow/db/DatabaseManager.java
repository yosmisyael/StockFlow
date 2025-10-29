package com.oop.stockflow.db;

import java.sql.Connection;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Manages database connections using HikariCP connection pooling.
 * Provides centralized access to database connections throughout the application.
 * Automatically initializes the connection pool on class loading and manages
 * connection lifecycle with optimal performance settings.
 */
public class DatabaseManager {
    private static HikariDataSource dataSource;

    /**
     * Constructor for the DatabaseManager class.
     *
     * This class is the central point for managing database connections,
     * executing SQL queries, and handling transactions for the entire application.
     * Initialization logic, such as configuring the connection pool or establishing
     * a connection, is typically performed here.
     *
     * @return A new instance of DatabaseManager.
     */
    public DatabaseManager() {}

    /**
     * Static initializer block that sets up the HikariCP connection pool.
     * Loads database credentials from environment variables (.env file) and configures
     * the connection pool with optimal settings for performance and resource management.
     * This block executes once when the class is first loaded.
     *
     * Configuration includes:
     * - Maximum pool size: 10 connections
     * - Idle timeout: 30 seconds
     * - Leak detection threshold: 20 seconds
     *
     * @throws Exception If database connection fails or environment variables are missing.
     */
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

    /**
     * Retrieves a database connection from the HikariCP connection pool.
     * The connection should be closed after use to return it to the pool.
     *
     * @return A Connection object from the pool.
     * @throws SQLException If unable to obtain a connection from the pool.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes the HikariCP connection pool and releases all database resources.
     * Should be called during application shutdown to properly clean up connections.
     *
     * @throws SQLException If an error occurs while closing the connection pool.
     */
    public static void closeDataSource() throws SQLException {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("[INFO] Connection pool closed.");
        }
    }
}
