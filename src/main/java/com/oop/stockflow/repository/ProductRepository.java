package com.oop.stockflow.repository;

import com.oop.stockflow.db.DatabaseManager;
import com.oop.stockflow.model.DryGoodProduct;
import com.oop.stockflow.model.FreshProduct;
import com.oop.stockflow.model.Product;
import com.oop.stockflow.model.ProductType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for handling product-related database operations.
 * Implements singleton pattern to ensure only one instance manages product data.
 * Supports operations for both DryGoodProduct and FreshProduct types including
 * CRUD operations, inventory queries, and warehouse-specific product management.
 */
public class ProductRepository {
    private static ProductRepository instance;

    /**
     * Private constructor to prevent direct instantiation.
     * Ensures only one instance can be created through getInstance().
     */
    private ProductRepository() {}

    /**
     * Returns the singleton instance of the ProductRepository.
     * Uses lazy initialization (creates instance on first call).
     * Consider changing to eager initialization for thread safety if needed:
     * private static final ProductRepository instance = new ProductRepository();
     * public static ProductRepository getInstance() { return instance; }
     *
     * @return The singleton ProductRepository instance.
     */
    public static ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    /**
     * Adds a new product (DryGoodProduct or FreshProduct) to the database.
     *
     * @param product The Product object (must be DryGoodProduct or FreshProduct).
     * @return true if insertion was successful, false otherwise.
     */
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, brand, description, purchase_price, " +
                "weight_per_unit_kg, volume_per_unit_m3, quantity, product_type, " +
                "reorder_point, reorder_quantity, units_per_case, " +
                "required_temp, days_to_alert_before_expiry, warehouse_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?::product_type, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set common fields (Indices 1 to 8)
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getBrand());
            stmt.setString(3, product.getDescription());
            stmt.setBigDecimal(4, product.getPurchasePrice());
            stmt.setDouble(5, product.getWeightPerUnitKg());
            stmt.setDouble(6, product.getVolumePerUnitM3());
            stmt.setInt(7, product.getQuantity());
            stmt.setString(8, product.getProductType().getDbValue());
            stmt.setInt(14, product.getWarehouseId());

            // Set specific fields based on type (Indices 9 to 13)
            if (product instanceof DryGoodProduct dryGood) {
                stmt.setInt(9, dryGood.getReorderPoint());      // Index 9
                stmt.setInt(10, dryGood.getReorderQuantity()); // Index 10
                stmt.setInt(11, dryGood.getUnitsPerCase());     // Index 11
                // Set Fresh fields to NULL
                stmt.setNull(12, Types.NUMERIC);               // Index 12
                stmt.setNull(13, Types.INTEGER);               // Index 13
            } else if (product instanceof FreshProduct fresh) {
                // Set Dry Good fields to NULL
                stmt.setNull(9, Types.INTEGER);                // Index 9
                stmt.setNull(10, Types.INTEGER);               // Index 10
                stmt.setNull(11, Types.INTEGER);               // Index 11
                // Set Fresh fields
                stmt.setBigDecimal(12, fresh.getRequiredTemp());  // Index 12
                stmt.setInt(13, fresh.getDaysToAlertBeforeExpiry()); // Index 13
            } else {
                System.err.println("[ERROR] Unknown product subclass type during addProduct.");
                return false;
            }

            // --- NO MORE setXXX(14, ...) ---

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // Updated error message to be safe with potentially null SKU
            System.err.println("[ERROR] Failed to add product " + product.getName() +
                    " (SKU: " + (product.getSku() != null ? product.getSku() : "New") + "): " +
                    e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all products belonging to a specific warehouse from the database.
     *
     * @param warehouseId The ID of the warehouse whose products to retrieve.
     * @return A List of Product objects (instantiated as DryGoodProduct or FreshProduct).
     */
    public List<Product> getAllProductsByWarehouseId(int warehouseId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT sku, name, brand, description, purchase_price, weight_per_unit_kg, " +
                "volume_per_unit_m3, quantity, product_type, reorder_point, reorder_quantity, " +
                "units_per_case, required_temp, days_to_alert_before_expiry, warehouse_id " +
                "FROM products WHERE warehouse_id = ? ORDER BY name";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, warehouseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    if (product != null) {
                        products.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] "  + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Retrieves all products belonging to a specific warehouse from the database, ordered by name.
     *
     * @param warehouseId The ID of the warehouse whose products to retrieve.
     * @return A List of Product objects (instantiated as DryGoodProduct or FreshProduct), or an empty list if none found or an error occurs.
     */
    public int countProductsByWarehouseId(int warehouseId) {
        String sql = "SELECT COUNT(*) FROM products WHERE warehouse_id = ?";
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
     * Counts the total number of products aggregated across all warehouses
     * owned by a specific manager.
     *
     * @param managerId The ID of the manager.
     * @return The total aggregated count of products, or -1 if an error occurs.
     */
    public int countProductsByManagerId(int managerId) {
        String sql = "SELECT COUNT(p.sku) " +
                "FROM products p " +
                "JOIN warehouses w ON p.warehouse_id = w.id " +
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
            System.err.println("[ERROR] Failed to count products by manager ID " + managerId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Counts the number of products considered "low stock" in a specific warehouse.
     *
     * @param warehouseId The ID of the warehouse.
     * @return The count of low stock products, or -1 if an error occurs.
     */
    public int countLowStockByWarehouseId(int warehouseId) {
        String sql = "SELECT COUNT(*) FROM products " +
                "WHERE warehouse_id = ? " +
                "AND (product_type = 'dry good'::product_type AND quantity < reorder_point)";

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
     * Counts the number of products considered "in stock" in a specific warehouse.
     *
     * @param warehouseId The ID of the warehouse.
     * @return The count of low stock products, or -1 if an error occurs.
     */
    public int countInStock(int warehouseId) {
        String sql = "SELECT COUNT(*) FROM products " +
                "WHERE warehouse_id = ? " +
                "AND (product_type = 'dry good'::product_type AND quantity >= reorder_point)";

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
     * Counts the number of products considered "out stock" in a specific warehouse.
     *
     * @param warehouseId The ID of the warehouse.
     * @return The count of low stock products, or -1 if an error occurs.
     */
    public int countOutStock(int warehouseId) {
        String sql = "SELECT COUNT(*) FROM products " +
                "WHERE warehouse_id = ? " +
                "AND (product_type = 'dry good'::product_type AND quantity = 0)";

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
     * Retrieves a single product by its SKU.
     * Note: Assumes SKU in Java is int, converts to String for DB query.
     *
     * @param sku The integer SKU of the product.
     * @return The Product object (DryGoodProduct or FreshProduct) if found, otherwise null.
     */
    public Product getProductBySku(int sku) {
        String sql = "SELECT sku, name, brand, description, purchase_price, weight_per_unit_kg, " +
                "volume_per_unit_m3, quantity, product_type, reorder_point, reorder_quantity, " +
                "units_per_case, required_temp, days_to_alert_before_expiry, warehouse_id " +
                "FROM products WHERE sku = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sku);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch product by SKU " + sku + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates an existing product's details (all fields except SKU).
     *
     * @param product The Product object containing the updated data.
     * @return true if update was successful, false otherwise.
     */
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, brand = ?, description = ?, purchase_price = ?, " +
                "weight_per_unit_kg = ?, volume_per_unit_m3 = ?, quantity = ?, product_type = ?::product_type, " +
                // Dry Good specific
                "reorder_point = ?, reorder_quantity = ?, units_per_case = ?, " +
                // Fresh specific
                "required_temp = ?, days_to_alert_before_expiry = ? " +
                "WHERE sku = ?"; // Update based on SKU

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set common fields
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getBrand());
            stmt.setString(3, product.getDescription());
            stmt.setBigDecimal(4, product.getPurchasePrice());
            stmt.setDouble(5, product.getWeightPerUnitKg());
            stmt.setDouble(6, product.getVolumePerUnitM3());
            stmt.setInt(7, product.getQuantity());
            stmt.setString(8, product.getProductType().getDbValue());

            // Set specific fields based on type
            if (product instanceof DryGoodProduct dryGood) {
                stmt.setInt(9, dryGood.getReorderPoint());
                stmt.setInt(10, dryGood.getReorderQuantity());
                stmt.setInt(11, dryGood.getUnitsPerCase());
                stmt.setNull(12, Types.NUMERIC);
                stmt.setNull(13, Types.INTEGER);
            } else if (product instanceof FreshProduct fresh) {
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
                stmt.setNull(11, Types.INTEGER);
                stmt.setBigDecimal(12, fresh.getRequiredTemp());
                stmt.setInt(13, fresh.getDaysToAlertBeforeExpiry());
            } else {
                System.err.println("[ERROR] Unknown product subclass type during updateProduct.");
                return false;
            }

            stmt.setInt(14, product.getSku());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to update product SKU " + product.getSku() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to map a ResultSet row to the correct Product subclass.
     *
     * @param rs The ResultSet positioned at the current row.
     * @return A DryGoodProduct or FreshProduct instance, or null if mapping fails.
     * @throws SQLException If a database access error occurs.
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        int sku = rs.getInt("sku");
        int warehouseId = rs.getInt("warehouse_id");
        String name = rs.getString("name");
        String brand = rs.getString("brand");
        String description = rs.getString("description");
        BigDecimal purchasePrice = rs.getBigDecimal("purchase_price");
        double weight = rs.getDouble("weight_per_unit_kg");
        double volume = rs.getDouble("volume_per_unit_m3");
        int quantity = rs.getInt("quantity");
        String typeString = rs.getString("product_type");

        ProductType type = ProductType.fromDbValue(typeString);

        if (type == null) {
            System.err.println("Warning: Unknown product type '" + typeString + "' for SKU " + sku);
            return null;
        }

        if (type == ProductType.DRY_GOOD) {
            int reorderPoint = rs.getInt("reorder_point");
            int reorderQuantity = rs.getInt("reorder_quantity");
            int unitsPerCase = rs.getInt("units_per_case");
            return new DryGoodProduct(sku, name, brand, description, purchasePrice, weight, volume,
                    quantity, reorderPoint, reorderQuantity, unitsPerCase, warehouseId);
        } else if (type == ProductType.FRESH) {
            BigDecimal requiredTemp = rs.getBigDecimal("required_temp");
            int daysAlert = rs.getInt("days_to_alert_before_expiry");
            return new FreshProduct(sku, name, brand, description, purchasePrice, weight, volume,
                    quantity, requiredTemp, daysAlert, warehouseId);
        } else {
            return null;
        }
    }

    /**
     * Retrieves only the name of a product based on its SKU.
     *
     * @param sku The integer SKU of the product.
     * @return The product name as a String if found, otherwise null.
     */
    public String getProductNameBySku(int sku) {
        String sql = "SELECT name FROM products WHERE sku = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sku);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves only the brand of a product based on its SKU.
     *
     * @param sku The integer SKU of the product.
     * @return The product brand as a String if found or defined, otherwise null.
     */
    public String getProductBrandBySku(int sku) {
        String sql = "SELECT brand FROM products WHERE sku = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sku);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("brand");
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
        return null;
    }

    /**
     * Deletes a product from the database based on its SKU.
     * Note: Depending on foreign key constraints (e.g., in transactions), this might fail
     * if the product is referenced elsewhere. Consider soft delete (adding an 'is_active' flag) instead.
     *
     * @param sku The integer SKU of the product to delete.
     * @return true if the product was successfully deleted (at least one row affected), false otherwise.
     */
    public boolean deleteProduct(int sku) {
        String sql = "DELETE FROM products WHERE sku = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sku);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
