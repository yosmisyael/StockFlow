package com.oop.stockflow.model;

/**
 * Represents a Manager user in the system, extending the base User class.
 * Managers have administrative privileges for warehouse operations and staff management.
 */
public class Manager extends User {
    /**
     * Constructs a Manager with the specified details.
     * Used when loading existing manager accounts from the database.
     *
     * @param id The unique identifier of the manager.
     * @param name The name of the manager.
     * @param email The email address of the manager.
     * @param warehouseId The warehouse ID assigned to the manager.
     */
    public Manager(int id, String name, String email, int warehouseId) {
        super.id = id;
        this.name = name;
        this.email = email;
    }
}
