package com.oop.stockflow.model;

public enum TransactionStatus {
    COMMITTED("committed"),
    PENDING("pending"),
    VOIDED("voided");

    private final String dbValue;

    TransactionStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static TransactionStatus fromDbValue(String value) {
        for (TransactionStatus type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction status: " + value);
    }
}
