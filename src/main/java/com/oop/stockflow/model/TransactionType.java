package com.oop.stockflow.model;

/**
 * Enumeration representing the different types of transactions in the warehouse system.
 * Maps Java enum constants to database string values for transaction direction classification.
 */
public enum TransactionType {
    INBOUND("inbound"),
    OUTBOUND("outbound");

    private final String dbValue;

    /**
     * Constructs a TransactionType enum with its corresponding database value.
     *
     * @param dbValue The string value stored in the database for this transaction type.
     */
    TransactionType(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Retrieves the database string value for this transaction type.
     *
     * @return The database representation of this transaction type.
     */
    public String getDbValue() {
        return dbValue;
    }

    /**
     * Converts a database string value to its corresponding TransactionType enum.
     * Case-insensitive matching is performed.
     *
     * @param value The database string value to convert.
     * @return The matching TransactionType enum constant.
     * @throws IllegalArgumentException If the value does not match any known transaction type.
     */
    public static TransactionType fromDbValue(String value) {
        for (TransactionType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + value);
    }
}
