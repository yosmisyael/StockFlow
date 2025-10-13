package com.oop.stockflow.model;

public enum WarehouseStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    MAINTENANCE("maintenance");

    private final String dbVal;

    WarehouseStatus(String value) {
        this.dbVal = value;
    }

    public String getDbVal() {
        return dbVal;
    }

    public static WarehouseStatus fromDbValue(String value) {
        for (WarehouseStatus status : values()) {
            if (status.dbVal.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown warehouse status: " + value);
    }
}
