package com.oop.stockflow.model;

public enum ProductType {
    DRY_GOOD("dry good"),
    FRESH("fresh");

    private final String dbValue;

    ProductType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ProductType fromDbValue(String value) {
        for (ProductType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown product type: " + value);
    }
}
