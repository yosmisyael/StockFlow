package com.oop.stockflow.model;

public enum UserType {
    MANAGER("manager"),
    STAFF("staff");

    private final String dbValue;

    UserType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static UserType fromDbValue(String value) {
        for (UserType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown user type: " + value);
    }
}
