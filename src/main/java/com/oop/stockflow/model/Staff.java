package com.oop.stockflow.model;

public class Staff extends User {
    private int warehouseId;

    public Staff(int id, String name, String email, int warehouseId) {
        super.id = id;
        this.name = name;
        this.email = email;
        this.warehouseId = warehouseId;
    }

    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }
}