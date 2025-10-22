package com.oop.stockflow.model;

public enum TransactionType {
    INBOUND("inbound"),
    OUTBOUND("outbound");

    private final String dbValue;

    TransactionType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static TransactionType fromDbValue(String value) {
        for (TransactionType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + value);
    }
}
