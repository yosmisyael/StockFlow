package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;
import com.oop.stockflow.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TransactionRepository {
    private static TransactionRepository instance;

    private TransactionRepository() {
    }

    /**
     * Returns the singleton instance of the TransactionRepository.
     * Creates the instance on the first call (lazy initialization).
     *
     * @return The singleton TransactionRepository instance.
     */
    public static TransactionRepository getInstance() {
        if (instance == null) {
            instance = new TransactionRepository();
        }
        return instance;
    }

    /**
     * Returns the singleton instance of the TransactionRepository.
     * Creates the instance on the first call (lazy initialization).
     *
     * @return The singleton TransactionRepository instance.
     */
    public boolean createInboundTransaction(int staffId, Timestamp date, ShippingType shippingMethod, int productSku, int quantity, TransactionStatus initialStatus) {
        String sql = "INSERT INTO transactions (user_id, date, transaction_type, destination_address, shipping_method, product_sku, quantity, status) " + "VALUES (?, ?, 'inbound'::transaction_type, NULL, ?::shipping_method, ?, ?, ?::transaction_status)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, staffId);
            stmt.setTimestamp(2, date);
            stmt.setString(3, shippingMethod.getDbValue());
            stmt.setInt(4, productSku);
            stmt.setInt(5, quantity);
            stmt.setString(6, initialStatus.getDbValue());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal membuat transaksi inbound: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a new outbound transaction in the database.
     *
     * @param staffId            The ID of the staff member creating the transaction.
     * @param date               The timestamp of the transaction.
     * @param destinationAddress The shipping destination address.
     * @param shippingMethod     The {@link ShippingType} enum value.
     * @param quantity           The quantity of the product.
     * @param productSku         The SKU (integer ID) of the product.
     * @param initialStatus      The initial {@link TransactionStatus} (e.g., PENDING).
     * @return {@code true} if the transaction was created successfully, {@code false} otherwise.
     */
    public boolean createOutboundTransaction(int staffId, Timestamp date, String destinationAddress, ShippingType shippingMethod, int quantity, int productSku, TransactionStatus initialStatus) {
        String sql = "INSERT INTO transactions (user_id, date, transaction_type, destination_address, shipping_method, quantity, product_sku, status) " + "VALUES (?, ?, 'outbound'::transaction_type, ?, ?::shipping_method, ?, ?, ?::transaction_status)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, staffId);
            stmt.setTimestamp(2, date);
            stmt.setString(3, destinationAddress);
            stmt.setString(4, shippingMethod.getDbValue());
            stmt.setInt(5, quantity);
            stmt.setInt(6, productSku);
            stmt.setString(7, initialStatus.getDbValue());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a list of all transactions (Inbound and Outbound) created by a specific staff member.
     * The list is ordered by date in descending order.
     *
     * @param staffId The ID of the staff member.
     * @return A {@code List<Transaction>} containing all transactions for the given staff member.
     * Returns an empty list if no transactions are found or an error occurs.
     */
    public List<Transaction> getAllTransactionsByStaffId(int staffId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, user_id, date, transaction_type, destination_address, shipping_method, product_sku, quantity, status " + "FROM transactions WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, staffId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = mapResultSetToTransaction(rs);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Counts the number of outbound transactions recorded today (since 00:00).
     *
     * @return The total count of today's outbound transactions, or -1 if an error occurs.
     */
    public int countTodayOutboundTransaction(int warehouseId) {
        String sql = "SELECT COUNT(t.*) FROM transactions t " + "JOIN products p ON t.product_sku = p.sku " + "WHERE t.transaction_type = 'outbound'::transaction_type " + "AND t.date >= CURRENT_DATE " + "AND t.date < CURRENT_DATE + interval '1 day' " + "AND p.warehouse_id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warehouseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Counts the number of inbound transactions recorded today (since 00:00) in a warehouse.
     *
     * @return The total count of today's inbound transactions, or -1 if an error occurs.
     */
    public int countTodayInboundTransaction(int warehouseId) {
        String sql = "SELECT COUNT(t.*) FROM transactions t " + "JOIN products p ON t.product_sku = p.sku " + "WHERE t.transaction_type = 'inbound'::transaction_type " + "AND t.date >= CURRENT_DATE " + "AND t.date < CURRENT_DATE + interval '1 day' " + "AND p.warehouse_id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warehouseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Updates only the status (e.g., PENDING to COMMITTED) of a specific transaction.
     *
     * @param transactionId The ID (BIGSERIAL) of the transaction to update.
     * @param newStatus     The new {@link TransactionStatus} to set.
     * @return {@code true} if the status was updated successfully, {@code false} otherwise.
     */
    public boolean updateTransactionStatus(long transactionId, TransactionStatus newStatus) {
        String sql = "UPDATE transactions SET status = ?::transaction_status WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.getDbValue());
            stmt.setLong(2, transactionId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to map a row from a ResultSet to the correct Transaction subclass
     * ({@link InboundTransaction} or {@link OutboundTransaction}) based on the 'transaction_type' column.
     *
     * @param rs The ResultSet, positioned at the row to map.
     * @return A {@code Transaction} object (as {@code InboundTransaction} or {@code OutboundTransaction}),
     * or {@code null} if the type is unknown or enum values are invalid.
     * @throws SQLException If an error occurs while reading from the ResultSet.
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        int id = (int) rs.getLong("id");
        int staffId = rs.getInt("user_id");
        Timestamp date = rs.getTimestamp("date");
        String typeString = rs.getString("transaction_type");
        String destAddress = rs.getString("destination_address");
        String shippingString = rs.getString("shipping_method");
        int productSku = rs.getInt("product_sku");
        int quantity = rs.getInt("quantity");
        String statusString = rs.getString("status");

        TransactionType type = TransactionType.fromDbValue(typeString);
        ShippingType shippingType = ShippingType.fromDbValue(shippingString);
        TransactionStatus status = TransactionStatus.fromDbValue(statusString);

        if (type == null || shippingType == null || status == null) {
            System.err.println("Warning: Invalid enum value found for transaction ID " + id);
            return null;
        }

        if (type == TransactionType.INBOUND) {
            return new InboundTransaction(id, productSku, staffId, quantity, date, shippingType, status, TransactionType.INBOUND);
        } else if (type == TransactionType.OUTBOUND) {
            return new OutboundTransaction(id, productSku, staffId, quantity, date, shippingType, status, destAddress, TransactionType.OUTBOUND);
        } else {
            System.err.println("Warning: Unknown transaction type found in DB: " + typeString);
            return null;
        }
    }

    /**
     * Gets the outbound transaction counts for a warehouse over the last N days.
     *
     * @param warehouseId The ID of the warehouse.
     * @param days The total number of days to retrieve (e.g., 7 for the last 7 days).
     * @return A Map<LocalDate, Integer> containing the counts for each day.
     * Days with no transactions will be included with a count of 0.
     */
    public Map<LocalDate, Integer> getOutboundTransactionCounts(int warehouseId, int days) {
        // Use LinkedHashMap to preserve date order
        Map<LocalDate, Integer> dailyCounts = new LinkedHashMap<>();

        // 1. Initialize all days with 0 counts
        // This ensures that even days with no transactions appear in the map
        LocalDate today = LocalDate.now();
        for (int i = 0; i < days; i++) {
            LocalDate date = today.minusDays((days - 1) - i); // Start from (days-1) ago up to today
            dailyCounts.put(date, 0);
        }

        String sql = "SELECT DATE(t.date) AS transaction_day, COUNT(t.*) AS transaction_count " +
                "FROM transactions t " +
                "JOIN products p ON t.product_sku = p.sku " +
                "WHERE t.transaction_type = 'outbound'::transaction_type " +
                "AND p.warehouse_id = ? " +
                "AND t.date >= (CURRENT_DATE - (? || ' days')::interval) " +
                "AND t.date < (CURRENT_DATE + '1 day'::interval) " +
                "GROUP BY transaction_day";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, warehouseId);
            stmt.setInt(2, days - 1);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("transaction_day").toLocalDate();
                    int count = rs.getInt("transaction_count");
                    dailyCounts.put(date, count);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
        }

        return dailyCounts;
    }
}
