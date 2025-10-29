package com.oop.stockflow.model;

/**
 * Enumeration representing the different shipping methods available for transactions.
 * Maps Java enum constants to database string values for shipping type classification.
 */
public enum ShippingType {
    STANDARD_GROUND("standard ground"),
    SEA_FREIGHT("sea freight"),
    EXPRESS_AIR("express air");

    private final String dbValue;

    /**
     * Constructs a ShippingType enum with its corresponding database value.
     *
     * @param dbValue The string value stored in the database for this shipping type.
     */
    ShippingType(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Retrieves the database string value for this shipping type.
     *
     * @return The database representation of this shipping type.
     */
    public String getDbValue() {
        return dbValue;
    }

    /**
     * Converts a database string value to its corresponding ShippingType enum.
     * Case-insensitive matching is performed.
     *
     * @param value The database string value to convert.
     * @return The matching ShippingType enum constant.
     * @throws IllegalArgumentException If the value does not match any known shipping type.
     */
    public static ShippingType fromDbValue(String value) {
        for (ShippingType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown user type: " + value);
    }
}
