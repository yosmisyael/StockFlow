package com.oop.stockflow.model;

/**
 * Abstract base class representing a user in the system.
 * Serves as the foundation for specific user types (e.g., Manager, Staff).
 * Contains common properties shared by all user types including credentials and basic information.
 */
public abstract class User {
    protected int id;
    protected String name;
    protected String email;
    protected String password;

    /**
     * Default constructor for User.
     * Creates an empty User instance to be initialized by subclasses.
     */
    public User() {}

    /**
     * Retrieves the unique identifier of this user.
     *
     * @return The user ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this user.
     *
     * @param id The user ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the name of this user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this user.
     *
     * @param name The user's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the email address of this user.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of this user.
     *
     * @param email The user's email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the password of this user.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of this user.
     *
     * @param password The user's password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
