package com.oop.stockflow.model;

import java.math.BigDecimal;

/**
 * Represents a Dry Good product, extending the base Product class.
 */
public class DryGoodProduct extends Product {

    private int reorderPoint;
    private int reorderQuantity;
    private int unitsPerCase;

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
    public int getReorderPoint() {
        return reorderPoint;
    }

    public int getReorderQuantity() {
        return reorderQuantity;
    }

    public int getUnitsPerCase() {
        return unitsPerCase;
    }

    // setter
    public void setReorderPoint(int reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public void setReorderQuantity(int reorderQuantity) {
        this.reorderQuantity = reorderQuantity;
    }

    public void setUnitsPerCase(int unitsPerCase) {
        this.unitsPerCase = unitsPerCase;
    }
}