package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;
import com.oop.stockflow.model.Warehouse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseRepository {

    private final Connection connection;

    public WarehouseRepository() {
        this.connection = DatabaseManager.getConnection();
    }

    public boolean insertWarehouse(String name, String address, int storageCapacityKgField, int storageCapacityM3Field, int manager_id) {
        String sql = "INSERT INTO warehouses (name, address, max_capacity_volume_m3, max_capacity_weight_kg, manager_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setInt(3, storageCapacityKgField);
            stmt.setInt(4, storageCapacityM3Field);
            stmt.setInt(5, manager_id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to insert warehouse: " + e.getMessage());
            return false;
        }
    }

    public List<Warehouse> getAllWarehouses() {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT id, name, address, max_capacity_volume_m3, max_capacity_weight_kg, manager_id FROM warehouses";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Warehouse w = new Warehouse(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getDouble("max_capacity_volume_m3"),
                        rs.getDouble("max_capacity_weight_kg"),
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
