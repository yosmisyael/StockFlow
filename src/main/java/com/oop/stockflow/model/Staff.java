package com.oop.stockflow.model;

/**
 * Represents a Staff user in the system, extending the base User class.
 * Staff members are assigned to specific warehouses and handle day-to-day operations.
 */
public class Staff extends User {
    private int warehouseId;

    /**
     * Constructs a Staff with the specified details.
     * Used when loading existing staff accounts from the database.
     *
     * @param id The unique identifier of the staff member.
     * @param name The name of the staff member.
     * @param email The email address of the staff member.
     * @param warehouseId The warehouse ID where the staff member is assigned.
     */
    public Staff(int id, String name, String email, int warehouseId) {
        super.id = id;
        this.name = name;
        this.email = email;
        this.warehouseId = warehouseId;
    }

    /**
     * Retrieves the warehouse ID where this staff member is assigned.
     *
     * @return The warehouse identifier.
     */
    public int getWarehouseId() { return warehouseId; }

    /**
     * Sets the warehouse ID where this staff member is assigned.
     *
     * @param warehouseId The warehouse identifier.
     */
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }
}