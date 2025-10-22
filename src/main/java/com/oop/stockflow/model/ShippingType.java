package com.oop.stockflow.model;

public enum ShippingType {
    STANDARD_GROUND("standard ground"),
    SEA_FREIGHT("sea freight"),
    EXPRESS_AIR("express air");

    private final String dbValue;

    ShippingType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ShippingType fromDbValue(String value) {
        for (ShippingType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown user type: " + value);
    }
}
