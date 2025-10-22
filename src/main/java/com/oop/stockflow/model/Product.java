package com.oop.stockflow.model;

import java.math.BigDecimal;

/**
 * Abstract base class representing a product in the inventory.
 * Corresponds to the common columns in the 'products' table.
 */
public abstract class Product {

    protected int sku;
    protected String name;
    protected String brand;
    protected String description;
    protected BigDecimal purchasePrice;
    protected double weightPerUnitKg;
    protected double volumePerUnitM3;
    protected int quantity;
    protected ProductType productType;

    protected Product(int sku, String name, String brand, String description,
                      BigDecimal purchasePrice, double weightPerUnitKg, double volumePerUnitM3,
                      int quantity, ProductType productType) {
        this.sku = sku;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.weightPerUnitKg = weightPerUnitKg;
        this.volumePerUnitM3 = volumePerUnitM3;
        this.quantity = quantity;
        this.productType = productType;
    }

    // getter
    public int getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public double getWeightPerUnitKg() {
        return weightPerUnitKg;
    }

    public double getVolumePerUnitM3() {
        return volumePerUnitM3;
    }

    public int getQuantity() {
        return quantity;
    }

    public ProductType getProductType() {
        return productType;
    }

    // setter
    public void setSku(int sku) {
        this.sku = sku;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public void setWeightPerUnitKg(double weightPerUnitKg) {
        this.weightPerUnitKg = weightPerUnitKg;
    }

    public void setVolumePerUnitM3(double volumePerUnitM3) {
        this.volumePerUnitM3 = volumePerUnitM3;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    @Override
    public String toString() {
        return name + " (" + sku + ")";
    }
}