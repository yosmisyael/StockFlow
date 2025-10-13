package com.oop.stockflow.model;

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

    public Warehouse(int id, String name, String city, String state, String postalCode, String address, double maxCapacityVolume, double maxCapacityWeight,  String status, int manager_id) {
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

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public double getMaxCapacityVolume() {
        return maxCapacityVolume;
    }

    public double getMaxCapacityWeight() {
        return maxCapacityWeight;
    }

    public int getManager_id() {
        return manager_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMaxCapacityVolume(double maxCapacityVolume) {
        this.maxCapacityVolume = maxCapacityVolume;
    }

    public void setMaxCapacityWeight(double maxCapacityWeight) {
        this.maxCapacityWeight = maxCapacityWeight;
    }

    public void setManager_id(int manager_id) {
        this.manager_id = manager_id;
    }
}
