package com.oop.stockflow.model;

/**
 * Enumeration representing the different types of users in the system.
 * Maps Java enum constants to database string values for user role classification.
 */
public enum UserType {
    MANAGER("manager"),
    STAFF("staff");

    private final String dbValue;

    /**
     * Constructs a UserType enum with its corresponding database value.
     *
     * @param dbValue The string value stored in the database for this user type.
     */
    UserType(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Retrieves the database string value for this user type.
     *
     * @return The database representation of this user type.
     */
    public String getDbValue() {
        return dbValue;
    }

    /**
     * Converts a database string value to its corresponding UserType enum.
     * Case-insensitive matching is performed.
     *
     * @param value The database string value to convert.
     * @return The matching UserType enum constant.
     * @throws IllegalArgumentException If the value does not match any known user type.
     */
    public static UserType fromDbValue(String value) {
        for (UserType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown user type: " + value);
    }
}
