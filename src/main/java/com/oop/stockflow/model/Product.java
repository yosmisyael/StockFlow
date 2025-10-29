package com.oop.stockflow.model;

import java.math.BigDecimal;

/**
 * Abstract base class representing a product in the inventory.
 * Corresponds to the common columns in the 'products' table.
 */
public abstract class Product {

    protected Integer sku;
    protected String name;
    protected String brand;
    protected String description;
    protected BigDecimal purchasePrice;
    protected double weightPerUnitKg;
    protected double volumePerUnitM3;
    protected int quantity;
    protected ProductType productType;
    protected int warehouseId;

    /**
     * Constructs a Product with all details including SKU.
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
     * @param productType The type of product (e.g., DRY_GOOD, FRESH).
     * @param warehouseId The warehouse where the product is stored.
     */
    protected Product(Integer sku, String name, String brand, String description,
                      BigDecimal purchasePrice, double weightPerUnitKg, double volumePerUnitM3,
                      int quantity, ProductType productType, int warehouseId) {
        this.sku = sku;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.weightPerUnitKg = weightPerUnitKg;
        this.volumePerUnitM3 = volumePerUnitM3;
        this.quantity = quantity;
        this.productType = productType;
        this.warehouseId = warehouseId;
    }

    /**
     * Constructs a new Product without SKU.
     * Used when creating new products before database insertion.
     * Delegates to the full constructor with null SKU.
     *
     * @param name The product name.
     * @param brand The product brand.
     * @param description The product description.
     * @param purchasePrice The purchase price per unit.
     * @param weightPerUnitKg The weight per unit in kilograms.
     * @param volumePerUnitM3 The volume per unit in cubic meters.
     * @param quantity The initial quantity in stock.
     * @param productType The type of product (e.g., DRY_GOOD, FRESH).
     * @param warehouseId The warehouse where the product will be stored.
     */
    protected Product(String name, String brand, String description,
                      BigDecimal purchasePrice, double weightPerUnitKg, double volumePerUnitM3,
                      int quantity, ProductType productType, int warehouseId) {
        this(null, name, brand, description, purchasePrice, weightPerUnitKg, volumePerUnitM3, quantity, productType, warehouseId);
    }

    // getter
    /**
     * Retrieves the warehouse ID where this product is stored.
     *
     * @return The warehouse identifier.
     */
    public int getWarehouseId() {
        return warehouseId;
    }

    /**
     * Retrieves the stock keeping unit (SKU) of this product.
     *
     * @return The unique SKU identifier, or null if not yet assigned.
     */
    public Integer getSku() {
        return sku;
    }

    /**
     * Retrieves the name of this product.
     *
     * @return The product name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the brand of this product.
     *
     * @return The product brand.
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Retrieves the description of this product.
     *
     * @return The product description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the purchase price per unit of this product.
     *
     * @return The purchase price.
     */
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Retrieves the weight per unit of this product.
     *
     * @return The weight in kilograms.
     */
    public double getWeightPerUnitKg() {
        return weightPerUnitKg;
    }

    /**
     * Retrieves the volume per unit of this product.
     *
     * @return The volume in cubic meters.
     */
    public double getVolumePerUnitM3() {
        return volumePerUnitM3;
    }

    /**
     * Retrieves the current quantity in stock for this product.
     *
     * @return The quantity in stock.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Retrieves the type of this product.
     *
     * @return The ProductType enum (e.g., DRY_GOOD, FRESH).
     */
    public ProductType getProductType() {
        return productType;
    }

    // setter
    /**
     * Sets the warehouse ID where this product is stored.
     *
     * @param warehouseId The warehouse identifier.
     */
    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    /**
     * Sets the stock keeping unit (SKU) for this product.
     *
     * @param sku The unique SKU identifier.
     */
    public void setSku(Integer sku) {
        this.sku = sku;
    }

    /**
     * Sets the name of this product.
     *
     * @param name The product name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the brand of this product.
     *
     * @param brand The product brand.
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Sets the description of this product.
     *
     * @param description The product description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the purchase price per unit of this product.
     *
     * @param purchasePrice The purchase price.
     */
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    /**
     * Sets the weight per unit of this product.
     *
     * @param weightPerUnitKg The weight in kilograms.
     */
    public void setWeightPerUnitKg(double weightPerUnitKg) {
        this.weightPerUnitKg = weightPerUnitKg;
    }

    /**
     * Sets the volume per unit of this product.
     *
     * @param volumePerUnitM3 The volume in cubic meters.
     */
    public void setVolumePerUnitM3(double volumePerUnitM3) {
        this.volumePerUnitM3 = volumePerUnitM3;
    }

    /**
     * Sets the current quantity in stock for this product.
     *
     * @param quantity The quantity in stock.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the type of this product.
     *
     * @param productType The ProductType enum (e.g., DRY_GOOD, FRESH).
     */
    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    /**
     * Returns a string representation of this product.
     * Format: "name (sku)"
     *
     * @return A string containing the product name and SKU.
     */
    @Override
    public String toString() {
        return name + " (" + sku + ")";
    }
}