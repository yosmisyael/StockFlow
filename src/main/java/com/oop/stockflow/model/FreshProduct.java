package com.oop.stockflow.model;

import java.math.BigDecimal;

/**
 * Represents a Fresh product, extending the base Product class.
 */
public class FreshProduct extends Product {

    private BigDecimal requiredTemp;
    private int daysToAlertBeforeExpiry;

    public FreshProduct(Integer sku, String name, String brand, String description,
                        BigDecimal purchasePrice, double weightPerUnitKg, double volumePerUnitM3,
                        int quantity,
                        BigDecimal requiredTemp, int daysToAlertBeforeExpiry, int warehouseId) {
        super(sku, name, brand, description, purchasePrice, weightPerUnitKg, volumePerUnitM3,
                quantity, ProductType.FRESH, warehouseId);
        this.requiredTemp = requiredTemp;
        this.daysToAlertBeforeExpiry = daysToAlertBeforeExpiry;
    }

    public FreshProduct(String name, String brand, String description,
                        BigDecimal purchasePrice, double weightPerUnitKg, double volumePerUnitM3,
                        int quantity,
                        BigDecimal requiredTemp, int daysToAlertBeforeExpiry, int warehouseId) {
        super(name, brand, description, purchasePrice, weightPerUnitKg, volumePerUnitM3,
                quantity, ProductType.FRESH, warehouseId);
        this.requiredTemp = requiredTemp;
        this.daysToAlertBeforeExpiry = daysToAlertBeforeExpiry;
    }

    // getter
    public BigDecimal getRequiredTemp() {
        return requiredTemp;
    }

    public int getDaysToAlertBeforeExpiry() {
        return daysToAlertBeforeExpiry;
    }

    // setter
    public void setRequiredTemp(BigDecimal requiredTemp) {
        this.requiredTemp = requiredTemp;
    }

    public void setDaysToAlertBeforeExpiry(int daysToAlertBeforeExpiry) {
        this.daysToAlertBeforeExpiry = daysToAlertBeforeExpiry;
    }
}