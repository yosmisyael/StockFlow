package com.oop.stockflow.model;

public class AuthenticatedUser extends User {
    private final UserType userType;

    public AuthenticatedUser(int id, String name, UserType userType) {
        this.id = id;
        this.name = name;
        this.userType = userType;
    }

    public UserType getUserType() {
        return userType;
    }
}
