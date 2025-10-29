package com.oop.stockflow.model;

/**
 * Enumeration representing the different statuses of a transaction in the system.
 * Maps Java enum constants to database string values for transaction status tracking.
 */
public enum TransactionStatus {
    COMMITTED("committed"),
    PENDING("pending"),
    VOIDED("voided");

    private final String dbValue;

    /**
     * Constructs a TransactionStatus enum with its corresponding database value.
     *
     * @param dbValue The string value stored in the database for this transaction status.
     */
    TransactionStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Retrieves the database string value for this transaction status.
     *
     * @return The database representation of this transaction status.
     */
    public String getDbValue() {
        return dbValue;
    }

    /**
     * Converts a database string value to its corresponding TransactionStatus enum.
     * Case-insensitive matching is performed.
     *
     * @param value The database string value to convert.
     * @return The matching TransactionStatus enum constant.
     * @throws IllegalArgumentException If the value does not match any known transaction status.
     */
    public static TransactionStatus fromDbValue(String value) {
        for (TransactionStatus type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction status: " + value);
    }
}
