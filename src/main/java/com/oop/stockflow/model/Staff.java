package com.oop.stockflow.model;

public class Staff {
    private final int id;
    private String name;
    private String email;
    private int warehouseId;

    public Staff(int id, String name, String email, int warehouseId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.warehouseId = warehouseId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getWarehouseId() { return warehouseId; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }
}