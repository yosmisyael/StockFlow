package com.oop.stockflow.model;

import java.math.BigDecimal;

/**
 * Represents a Dry Good product, extending the base Product class.
 */
public class DryGoodProduct extends Product {

    private int reorderPoint;
    private int reorderQuantity;
    private int unitsPerCase;

    /**
     * Constructs a DryGoodProduct with all details including SKU.
     * Used when loading existing products from the database.
     *
     * @param sku The unique stock keeping unit identifier.
     * @param name The product name.
     * @param brand The product brand.
     * @param description The product description.
     * @param purchasePrice The purchase price per unit.
     * @param weightPerUnitKg The weight per unit in kilograms.
     * @param volumePerUnitM3 The volume per unit in cubic meters.
     * @param quantity The current quantity in stock.
     * @param reorderPoint The inventory level that triggers reordering.
     * @param reorderQuantity The quantity to order when reorder point is reached.
     * @param unitsPerCase The number of units packaged per case.
     * @param warehouseId The warehouse where the product is stored.
     */
    public DryGoodProduct(Integer sku, String name, String brand, String description,
                          BigDecimal purchasePrice, double weightPerUnitKg, double volumePerUnitM3,
                          int quantity,
                          int reorderPoint, int reorderQuantity, int unitsPerCase, int warehouseId) {
        super(sku, name, brand, description, purchasePrice, weightPerUnitKg, volumePerUnitM3,
                quantity, ProductType.DRY_GOOD, warehouseId);
        this.reorderPoint = reorderPoint;
        this.reorderQuantity = reorderQuantity;
        this.unitsPerCase = unitsPerCase;
    }

    /**
     * Constructs a new DryGoodProduct without SKU.
     * Used when creating new products before database insertion.
     *
     * @param name The product name.
     * @param brand The product brand.
     * @param description The product description.
     * @param purchasePrice The purchase price per unit.
     * @param weightPerUnitKg The weight per unit in kilograms.
     * @param volumePerUnitM3 The volume per unit in cubic meters.
     * @param quantity The initial quantity in stock.
     * @param reorderPoint The inventory level that triggers reordering.
     * @param reorderQuantity The quantity to order when reorder point is reached.
     * @param unitsPerCase The number of units packaged per case.
     * @param warehouseId The warehouse where the product will be stored.
     */
    public DryGoodProduct(String name, String brand, String description,
                          BigDecimal purchasePrice, double weightPerUnitKg, double volumePerUnitM3,
                          int quantity,
                          int reorderPoint, int reorderQuantity, int unitsPerCase, int warehouseId) {
        super(name, brand, description, purchasePrice, weightPerUnitKg, volumePerUnitM3,
                quantity, ProductType.DRY_GOOD, warehouseId);
        this.reorderPoint = reorderPoint;
        this.reorderQuantity = reorderQuantity;
        this.unitsPerCase = unitsPerCase;
    }

    // getter
    /**
     * Retrieves the reorder point for this product.
     *
     * @return The inventory level that triggers reordering.
     */
    public int getReorderPoint() {
        return reorderPoint;
    }

    /**
     * Retrieves the reorder quantity for this product.
     *
     * @return The quantity to order when reorder point is reached.
     */
    public int getReorderQuantity() {
        return reorderQuantity;
    }

    /**
     * Retrieves the units per case for this product.
     *
     * @return The number of units packaged per case.
     */
    public int getUnitsPerCase() {
        return unitsPerCase;
    }

    // setter
    /**
     * Sets the reorder point for this product.
     *
     * @param reorderPoint The inventory level that triggers reordering.
     */
    public void setReorderPoint(int reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    /**
     * Sets the reorder quantity for this product.
     *
     * @param reorderQuantity The quantity to order when reorder point is reached.
     */
    public void setReorderQuantity(int reorderQuantity) {
        this.reorderQuantity = reorderQuantity;
    }

    /**
     * Sets the units per case for this product.
     *
     * @param unitsPerCase The number of units packaged per case.
     */
    public void setUnitsPerCase(int unitsPerCase) {
        this.unitsPerCase = unitsPerCase;
    }
}