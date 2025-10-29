package com.oop.stockflow.model;

import java.math.BigDecimal;

/**
 * Represents a Fresh product, extending the base Product class.
 */
public class FreshProduct extends Product {

    private BigDecimal requiredTemp;
    private int daysToAlertBeforeExpiry;

    /**
     * Constructs a FreshProduct with all details including SKU.
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
     * @param requiredTemp The required storage temperature in degrees Celsius.
     * @param daysToAlertBeforeExpiry The number of days before expiry to trigger an alert.
     * @param warehouseId The warehouse where the product is stored.
     */
    public FreshProduct(Integer sku, String name, String brand, String description,
                        BigDecimal purchasePrice, double weightPerUnitKg, double volumePerUnitM3,
                        int quantity,
                        BigDecimal requiredTemp, int daysToAlertBeforeExpiry, int warehouseId) {
        super(sku, name, brand, description, purchasePrice, weightPerUnitKg, volumePerUnitM3,
                quantity, ProductType.FRESH, warehouseId);
        this.requiredTemp = requiredTemp;
        this.daysToAlertBeforeExpiry = daysToAlertBeforeExpiry;
    }

    /**
     * Constructs a new FreshProduct without SKU.
     * Used when creating new products before database insertion.
     *
     * @param name The product name.
     * @param brand The product brand.
     * @param description The product description.
     * @param purchasePrice The purchase price per unit.
     * @param weightPerUnitKg The weight per unit in kilograms.
     * @param volumePerUnitM3 The volume per unit in cubic meters.
     * @param quantity The initial quantity in stock.
     * @param requiredTemp The required storage temperature in degrees Celsius.
     * @param daysToAlertBeforeExpiry The number of days before expiry to trigger an alert.
     * @param warehouseId The warehouse where the product will be stored.
     */
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
    /**
     * Retrieves the required storage temperature for this product.
     *
     * @return The required temperature in degrees Celsius.
     */
    public BigDecimal getRequiredTemp() {
        return requiredTemp;
    }

    /**
     * Retrieves the number of days before expiry to trigger an alert.
     *
     * @return The alert threshold in days before expiry.
     */
    public int getDaysToAlertBeforeExpiry() {
        return daysToAlertBeforeExpiry;
    }

    // setter
    /**
     * Sets the required storage temperature for this product.
     *
     * @param requiredTemp The required temperature in degrees Celsius.
     */
    public void setRequiredTemp(BigDecimal requiredTemp) {
        this.requiredTemp = requiredTemp;
    }

    /**
     * Sets the number of days before expiry to trigger an alert.
     *
     * @param daysToAlertBeforeExpiry The alert threshold in days before expiry.
     */
    public void setDaysToAlertBeforeExpiry(int daysToAlertBeforeExpiry) {
        this.daysToAlertBeforeExpiry = daysToAlertBeforeExpiry;
    }
}