package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.model.WarehouseStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseRepository {

    private final Connection connection;

    public WarehouseRepository() {
        this.connection = DatabaseManager.getConnection();
    }

    public boolean insertWarehouse(String name, String address, String city, String state, String postalCode, int storageCapacityKgField, int storageCapacityM3Field, WarehouseStatus status, int manager_id) {
        String sql = "INSERT INTO warehouses (name, address, city, state, postal_code, max_capacity_volume_m3, max_capacity_weight_kg, status, manager_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?::warehouse_status, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

    public List<Warehouse> getAllWarehouses() {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM warehouses";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Warehouse w = new Warehouse(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("postal_code"),
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
