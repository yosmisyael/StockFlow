package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;
import com.oop.stockflow.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private static TransactionRepository instance;
    private TransactionRepository() {}

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
    public boolean createInboundTransaction(int staffId, Timestamp date, ShippingType shippingMethod,
                                            int productSku, int quantity, TransactionStatus initialStatus) {
        String sql = "INSERT INTO transactions (user_id, date, transaction_type, destination_address, shipping_method, product_sku, quantity, status) " +
                "VALUES (?, ?, 'inbound'::transaction_type, NULL, ?::shipping_method, ?, ?, ?::transaction_status)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
    public boolean createOutboundTransaction(int staffId, Timestamp date, String destinationAddress,
                                             ShippingType shippingMethod,
                                             int quantity, int productSku, TransactionStatus initialStatus) {
        String sql = "INSERT INTO transactions (user_id, date, transaction_type, destination_address, shipping_method, quantity, product_sku, status) " +
                "VALUES (?, ?, 'outbound'::transaction_type, ?, ?::shipping_method, ?, ?, ?::transaction_status)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        String sql = "SELECT id, user_id, date, transaction_type, destination_address, shipping_method, product_sku, quantity, status " +
                "FROM transactions WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
    public int countTodayOutboundTransaction() {
        String sql = "SELECT COUNT(*) FROM transactions " +
                "WHERE transaction_type = 'outbound'::transaction_type " +
                "AND date >= CURRENT_DATE " +
                "AND date < CURRENT_DATE + interval '1 day'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
     * Counts the number of inbound transactions recorded today (since 00:00).
     *
     * @return The total count of today's inbound transactions, or -1 if an error occurs.
     */
    public int countTodayInboundTransaction() {
        String sql = "SELECT COUNT(*) FROM transactions " +
                "WHERE transaction_type = 'inbound'::transaction_type " +
                "AND date >= CURRENT_DATE " +
                "AND date < CURRENT_DATE + interval '1 day'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Ambil hasil COUNT
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

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        int id = (int)rs.getLong("id");
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
}
