package com.oop.stockflow.model;

/**
 * Enumeration representing the different operational statuses of a warehouse.
 * Maps Java enum constants to database string values for warehouse status classification.
 */
public enum WarehouseStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    MAINTENANCE("maintenance");

    private final String dbVal;

    /**
     * Constructs a WarehouseStatus enum with its corresponding database value.
     *
     * @param value The string value stored in the database for this warehouse status.
     */
    WarehouseStatus(String value) {
        this.dbVal = value;
    }

    /**
     * Retrieves the database string value for this warehouse status.
     *
     * @return The database representation of this warehouse status.
     */
    public String getDbVal() {
        return dbVal;
    }

    /**
     * Converts a database string value to its corresponding WarehouseStatus enum.
     * Case-insensitive matching is performed.
     *
     * @param value The database string value to convert.
     * @return The matching WarehouseStatus enum constant.
     * @throws IllegalArgumentException If the value does not match any known warehouse status.
     */
    public static WarehouseStatus fromDbValue(String value) {
        for (WarehouseStatus status : values()) {
            if (status.dbVal.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown warehouse status: " + value);
    }
}
