package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;
import com.oop.stockflow.model.Staff;
import org.mindrot.jbcrypt.BCrypt; // <-- Pastikan Anda memiliki library BCrypt

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {

    // Constructor kosong (sesuai pola perbaikan)
    public StaffRepository() {
    }

    /**
     * Membuat staf baru di database.
     * Method ini secara otomatis mengenkripsi password.
     */
    public boolean createStaff(String fullName, String username, String plainPassword, int warehouseId) {
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        String sql = "INSERT INTO staff (name, email, password, warehouse_id) VALUES (?, ?, ?, 'Aktif')";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setString(2, username);
            stmt.setString(3, hashedPassword);
            stmt.setInt(4, warehouseId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal membuat staf: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Staff> getStaffByWarehouse(int warehouseId) {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT id, name, email, warehouse_id FROM staff WHERE warehouse_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, warehouseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Staff staff = new Staff(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("status"),
                            rs.getInt("warehouse_id")
                    );
                    staffList.add(staff);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal mengambil daftar staf: " + e.getMessage());
            e.printStackTrace();
        }
        return staffList;
    }

    public Staff getStaffById(int staffId) {
        String sql = "SELECT id, name, email, password, warehouse_id FROM staff WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, staffId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Staff(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("status"),
                            rs.getInt("warehouse_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal mengambil detail staf: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Mengembalikan null jika tidak ditemukan
    }

    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE staff SET name = ?, email = ?, warehouse_id = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getName());
            stmt.setString(2, staff.getEmail());
            stmt.setInt(3, staff.getWarehouseId());
            stmt.setInt(4, staff.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal mengupdate staf: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM staff WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, staffId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}