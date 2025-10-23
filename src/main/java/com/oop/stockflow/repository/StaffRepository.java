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

    /**
     * Returns the singleton instance of the StaffRepository.
     * Creates the instance on the first call (lazy initialization).
     * Note: This simple lazy initialization is not thread-safe.
     * Consider eager initialization for better thread safety if needed.
     *
     * @return The singleton StaffRepository instance.
     */
    public static StaffRepository getInstance() {
        if (instance == null) {
            instance = new StaffRepository();
        }
        return instance;
    }

    /**
     * Creates a new staff member in the database with an encrypted password.
     * Assumes the 'status' column is handled automatically or defaults.
     *
     * @param name         The full name of the staff member.
     * @param email        The unique email address of the staff member (used as username).
     * @param password     The plain-text password to be encrypted.
     * @param warehouseId  The ID of the warehouse the staff member belongs to.
     * @return {@code true} if the staff member was created successfully, {@code false} otherwise.
     */
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

    /**
     * Retrieves a list of all staff members assigned to a specific warehouse.
     * Does not retrieve password information.
     *
     * @param warehouseId The ID of the warehouse.
     * @return A {@code List} of {@code Staff} objects, or an empty list if no staff are found or an error occurs.
     */
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

    /**
     * Retrieves the details of a single staff member by their ID.
     * Does not retrieve password information for security reasons.
     *
     * @param staffId The ID of the staff member to retrieve.
     * @return A {@code Staff} object if found, otherwise {@code null}.
     */
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
     * Counts the number of staff members assigned to a specific warehouse.
     *
     * @param warehouseId The ID of the warehouse.
     * @return The total count of staff members, or -1 if an error occurs.
     */
    public int countStaffByWarehouseId(int warehouseId) {
        String sql = "SELECT COUNT(*) FROM staff WHERE warehouse_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
     * Counts the total number of staff members assigned to all warehouses
     * that are managed by a specific manager.
     * This is a transitive count: Manager -> Warehouses -> Staff.
     *
     * @param managerId The ID of the manager.
     * @return The total count of all staff members working under that manager,
     * or -1 if an error occurs.
     */
    public int countAllStaffByManagerId(int managerId) {
        String sql = "SELECT COUNT(s.id) " +
                "FROM staff s " +
                "JOIN warehouses w ON s.warehouse_id = w.id " +
                "WHERE w.manager_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, managerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to count all staff by manager ID " + managerId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
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

    /**
     * Deletes a staff member from the database based on their ID.
     * Note: Depending on foreign key constraints (e.g., in transactions table referencing user_id),
     * the behavior might be restricted (e.g., setting user_id to NULL or preventing deletion).
     *
     * @param staffId The ID of the staff member to delete.
     * @return {@code true} if the staff member was successfully deleted (at least one row affected), {@code false} otherwise.
     */
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