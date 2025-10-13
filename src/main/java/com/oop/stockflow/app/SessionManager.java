package com.oop.stockflow.app;

import com.oop.stockflow.model.AuthenticatedUser;

public final class SessionManager {

    private static SessionManager instance;
    private AuthenticatedUser currentUser; // Changed from Manager to AuthenticatedUser

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void startSession(AuthenticatedUser user) {
        this.currentUser = user;
    }

    public void endSession() {
        this.currentUser = null;
    }

    public AuthenticatedUser getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}