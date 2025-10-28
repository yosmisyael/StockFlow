package com.oop.stockflow.model;

public class Manager extends User {
    public Manager(int id, String name, String email, int warehouseId) {
        super.id = id;
        this.name = name;
        this.email = email;
    }
}
