package com.oop.stockflow.model;

/**
 * Enumeration representing the different types of products in the inventory system.
 * Maps Java enum constants to database string values for product classification.
 */
public enum ProductType {
    DRY_GOOD("dry good"),
    FRESH("fresh");

    private final String dbValue;

    /**
     * Constructs a ProductType enum with its corresponding database value.
     *
     * @param dbValue The string value stored in the database for this product type.
     */
    ProductType(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Retrieves the database string value for this product type.
     *
     * @return The database representation of this product type.
     */
    public String getDbValue() {
        return dbValue;
    }

    /**
     * Converts a database string value to its corresponding ProductType enum.
     * Case-insensitive matching is performed.
     *
     * @param value The database string value to convert.
     * @return The matching ProductType enum constant.
     * @throws IllegalArgumentException If the value does not match any known product type.
     */
    public static ProductType fromDbValue(String value) {
        for (ProductType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown product type: " + value);
    }
}
