package com.oop.stockflow.model;

public class Staff {
    private final int id;
    private String name;
    private String email;
    private String status;
    private int warehouseId;


    public Staff(int id, String fullName, String username, String status, int warehouseId) {
        this.id = id;
        this.name = fullName;
        this.email = username;
        this.status = status;
        this.warehouseId = warehouseId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public int getWarehouseId() { return warehouseId; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setStatus(String status) { this.status = status; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }
}