package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;
import com.oop.stockflow.model.Staff;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {
    private static StaffRepository instance;

    private StaffRepository() {}

    public static StaffRepository getInstance() {
        if (instance == null) {
            instance = new StaffRepository();
        }
        return instance;
    }

    public boolean createStaff(String name, String email, String password, int warehouseId) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO staff (name, email, password, warehouse_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setInt(4, warehouseId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
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
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("warehouse_id")
                    );
                    staffList.add(staff);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
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
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getInt("warehouse_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mengupdate detail staf (nama, email, dan/atau password) secara opsional.
     * Kolom warehouse_id TIDAK diubah oleh method ini.
     *
     * @param staffId            ID staf yang akan diupdate.
     * @param newName            Nama baru, atau null jika tidak ingin mengubah nama.
     * @param newEmail           Email baru, atau null jika tidak ingin mengubah email.
     * @param newPlainTextPassword Password baru (plain text), atau null/kosong jika tidak ingin mengubah password.
     * @return true jika update berhasil (atau tidak ada yang diupdate), false jika gagal.
     */
    public boolean updateStaffDetails(int staffId, String newName, String newEmail, String newPlainTextPassword) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE staff SET ");
        List<Object> params = new ArrayList<>();
        boolean needsComma = false;

        // check name field update
        if (newName != null) {
            sqlBuilder.append("name = ?");
            params.add(newName);
            needsComma = true;
        }

        // check email field update
        if (newEmail != null) {
            if (needsComma) {
                sqlBuilder.append(", ");
            }
            sqlBuilder.append("email = ?");
            params.add(newEmail);
            needsComma = true;
        }

        // check password field update
        if (newPlainTextPassword != null && !newPlainTextPassword.isEmpty()) {
            if (needsComma) {
                sqlBuilder.append(", ");
            }
            String hashedPassword = BCrypt.hashpw(newPlainTextPassword, BCrypt.gensalt());
            sqlBuilder.append("password = ?");
            params.add(hashedPassword);
            needsComma = true;
        }

        // check if no update required
        if (params.isEmpty()) {
            System.out.println("[INFO] Tidak ada field yang diupdate untuk staff ID: " + staffId);
            return true;
        }

        // where clause
        sqlBuilder.append(" WHERE id = ?");
        params.add(staffId);

        String finalSql = sqlBuilder.toString();
        System.out.println("[INFO] Executing SQL: " + finalSql);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(finalSql)) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
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