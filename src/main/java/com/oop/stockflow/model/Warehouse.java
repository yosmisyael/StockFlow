package com.oop.stockflow.model;

/**
 * Represents a warehouse facility in the system.
 * Contains location details, capacity constraints, operational status, and assigned manager information.
 */
public class Warehouse {
    private int id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private WarehouseStatus status;
    private double maxCapacityVolume;
    private double maxCapacityWeight;
    private int manager_id;

    /**
     * Constructs a Warehouse with all specified details.
     * Used when loading existing warehouses from the database.
     *
     * @param id The unique identifier of the warehouse.
     * @param name The name of the warehouse.
     * @param city The city where the warehouse is located.
     * @param state The state where the warehouse is located.
     * @param postalCode The postal code of the warehouse location.
     * @param address The street address of the warehouse.
     * @param maxCapacityVolume The maximum storage capacity in cubic meters.
     * @param maxCapacityWeight The maximum weight capacity in kilograms.
     * @param status The operational status of the warehouse (e.g., "active", "inactive").
     * @param manager_id The ID of the manager assigned to this warehouse.
     */
    public Warehouse(int id, String name, String city, String state, String postalCode, String address, double maxCapacityVolume, double maxCapacityWeight, String status, int manager_id) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.status = WarehouseStatus.fromDbValue(status);
        this.address = address;
        this.maxCapacityVolume = maxCapacityVolume;
        this.maxCapacityWeight = maxCapacityWeight;
        this.manager_id = manager_id;
    }

    /**
     * Retrieves the operational status of this warehouse.
     *
     * @return The WarehouseStatus enum (e.g., ACTIVE, INACTIVE).
     */
    public WarehouseStatus getStatus() {
        return status;
    }

    /**
     * Sets the operational status of this warehouse.
     *
     * @param status The WarehouseStatus enum (e.g., ACTIVE, INACTIVE).
     */
    public void setStatus(WarehouseStatus status) {
        this.status = status;
    }

    /**
     * Retrieves the postal code of this warehouse.
     *
     * @return The postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code of this warehouse.
     *
     * @param postalCode The postal code.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Retrieves the state where this warehouse is located.
     *
     * @return The state name.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state where this warehouse is located.
     *
     * @param state The state name.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Retrieves the city where this warehouse is located.
     *
     * @return The city name.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city where this warehouse is located.
     *
     * @param city The city name.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Retrieves the name of this warehouse.
     *
     * @return The warehouse name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the unique identifier of this warehouse.
     *
     * @return The warehouse ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the street address of this warehouse.
     *
     * @return The street address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Retrieves the maximum storage capacity by volume of this warehouse.
     *
     * @return The maximum capacity in cubic meters.
     */
    public double getMaxCapacityVolume() {
        return maxCapacityVolume;
    }

    /**
     * Retrieves the maximum weight capacity of this warehouse.
     *
     * @return The maximum capacity in kilograms.
     */
    public double getMaxCapacityWeight() {
        return maxCapacityWeight;
    }

    /**
     * Retrieves the ID of the manager assigned to this warehouse.
     *
     * @return The manager's ID.
     */
    public int getManager_id() {
        return manager_id;
    }

    /**
     * Sets the name of this warehouse.
     *
     * @param name The warehouse name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the unique identifier of this warehouse.
     *
     * @param id The warehouse ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the street address of this warehouse.
     *
     * @param address The street address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Sets the maximum storage capacity by volume of this warehouse.
     *
     * @param maxCapacityVolume The maximum capacity in cubic meters.
     */
    public void setMaxCapacityVolume(double maxCapacityVolume) {
        this.maxCapacityVolume = maxCapacityVolume;
    }

    /**
     * Sets the maximum weight capacity of this warehouse.
     *
     * @param maxCapacityWeight The maximum capacity in kilograms.
     */
    public void setMaxCapacityWeight(double maxCapacityWeight) {
        this.maxCapacityWeight = maxCapacityWeight;
    }

    /**
     * Sets the ID of the manager assigned to this warehouse.
     *
     * @param manager_id The manager's ID.
     */
    public void setManager_id(int manager_id) {
        this.manager_id = manager_id;
    }
}
