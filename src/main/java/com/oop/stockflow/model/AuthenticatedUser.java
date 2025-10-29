package com.oop.stockflow.model;

/**
 * Represents an authenticated user in the system with their user type.
 * Extends the base User class and adds authentication-specific information.
 */
public class AuthenticatedUser extends User {
    private final UserType userType;

    /**
     * Constructs an authenticated user with the specified details.
     *
     * @param id The unique identifier of the user.
     * @param name The name of the user.
     * @param userType The type/role of the user (e.g., Admin, Customer, etc.).
     */
    public AuthenticatedUser(int id, String name, UserType userType) {
        this.id = id;
        this.name = name;
        this.userType = userType;
    }

    /**
     * Retrieves the user type/role of this authenticated user.
     *
     * @return The UserType enum representing the user's role in the system.
     */
    public UserType getUserType() {
        return userType;
    }
}
