package com.oop.stockflow.model;

public class AuthenticatedUser {
    private final int id;
    private final String name;
    private final UserType userType;

    public AuthenticatedUser(int id, String name, UserType userType) {
        this.id = id;
        this.name = name;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserType getUserType() {
        return userType;
    }
}
