package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;
import com.oop.stockflow.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private static TransactionRepository instance;
    private TransactionRepository() {}

    public static TransactionRepository getInstance() {
        if (instance == null) {
            instance = new TransactionRepository();
        }
        return instance;
    }

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
