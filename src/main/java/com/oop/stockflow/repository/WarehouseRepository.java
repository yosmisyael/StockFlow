package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;
import com.oop.stockflow.model.Staff;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.model.WarehouseStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseRepository {
    private static WarehouseRepository instance;

    private WarehouseRepository() {}

    /**
     * Returns the singleton instance of the WarehouseRepository.
     * Creates the instance on the first call (lazy initialization).
     *
     * @return The singleton WarehouseRepository instance.
     */
    public static WarehouseRepository getInstance() {
        if (instance == null) {
            instance = new WarehouseRepository();
        }
        return instance;
    }

    /**
     * Inserts a new warehouse record into the database.
     *
     * @param name                   The name of the warehouse.
     * @param address                The full address of the warehouse.
     * @param city                   The city where the warehouse is located.
     * @param state                  The state where the warehouse is located.
     * @param postalCode             The postal code of the warehouse.
     * @param storageCapacityKgField The maximum weight capacity in kilograms.
     * @param storageCapacityM3Field The maximum volume capacity in cubic meters.
     * @param status                 The operational status of the warehouse (enum WarehouseStatus).
     * @param manager_id             The ID of the manager assigned to this warehouse.
     * @return true if the warehouse was inserted successfully, false otherwise.
     */
    public boolean insertWarehouse(String name, String address, String city, String state, String postalCode, int storageCapacityKgField, int storageCapacityM3Field, WarehouseStatus status, int manager_id) {
        String sql = "INSERT INTO warehouses (name, address, city, state, postal_code, max_capacity_volume_m3, max_capacity_weight_kg, status, manager_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?::warehouse_status, ?)";

        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, city);
            stmt.setString(4, state);
            stmt.setString(5, postalCode);
            stmt.setInt(7, storageCapacityM3Field);
            stmt.setInt(6, storageCapacityKgField);
            stmt.setString(8, status.getDbVal());
            stmt.setInt(9, manager_id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to insert warehouse: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a single Warehouse object from the database based on its ID.
     *
     * @param warehouseId The ID of the warehouse to retrieve.
     * @return A Warehouse object if found, otherwise null.
     */
    public Warehouse getWarehouseById(int warehouseId) {
        String sql = "SELECT id, name, city, state, postal_code, address, status, " +
                "max_capacity_volume_m3, max_capacity_weight_kg, manager_id " +
                "FROM warehouses WHERE id = ?";

        Warehouse warehouse = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, warehouseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String city = rs.getString("city");
                    String state = rs.getString("state");
                    String postalCode = rs.getString("postal_code");
                    String address = rs.getString("address");
                    String statusStr = rs.getString("status");
                    double maxCapacityVolume = rs.getDouble("max_capacity_volume_m3");
                    double maxCapacityWeight = rs.getDouble("max_capacity_weight_kg");
                    int managerId = rs.getInt("manager_id");
                    if (rs.wasNull()) {
                        managerId = 0;
                    }

                    warehouse = new Warehouse(
                            id,
                            name,
                            city,
                            state,
                            postalCode,
                            address,
                            maxCapacityVolume,
                            maxCapacityWeight,
                            statusStr,
                            managerId
                    );
                } else {
                    System.out.println("[INFO] No warehouse found with ID: " + warehouseId);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch warehouse by ID " + warehouseId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return warehouse;
    }

    /**
     * Retrieves a list of all warehouses from the database.
     *
     * @return A List containing all Warehouse objects found, or an empty list if none exist or an error occurs.
     */
    public List<Warehouse> getAllWarehouses() {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM warehouses";

        try (Statement stmt = DatabaseManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Warehouse w = new Warehouse(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("postal_code"),
                        rs.getString("address"),
                        rs.getDouble("max_capacity_volume_m3"),
                        rs.getDouble("max_capacity_weight_kg"),
                        rs.getString("status"),
                        rs.getInt("manager_id")
                );
                warehouses.add(w);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to fetch warehouses: " + e.getMessage());
        }

        return warehouses;
    }
}
