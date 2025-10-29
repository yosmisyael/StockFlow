package com.oop.stockflow.app;

import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.repository.AuthRepository;

/**
 * Manages user session state throughout the application lifecycle.
 * Implements singleton pattern to ensure only one session manager exists.
 * Handles session creation, persistence to database, and cleanup during logout.
 */
public final class SessionManager {

    private static SessionManager instance;
    private AuthenticatedUser currentUser;

    private AuthRepository authRepository = AuthRepository.getInstance();

    /**
     * Private constructor to prevent direct instantiation.
     * Ensures only one instance can be created through getInstance().
     */
    private SessionManager() {}

    /**
     * Returns the singleton instance of SessionManager.
     * Creates a new instance if one doesn't exist yet (lazy initialization).
     *
     * @return The single SessionManager instance used throughout the application.
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Starts a new session: saves to DB and stores user locally.
     * Should be called AFTER successful login.
     *
     * @param user The successfully authenticated user object.
     * @return true if session was started successfully (DB save OK), false otherwise.
     */
    public boolean startSession(AuthenticatedUser user) {
        if (user == null) {
            System.err.println("[ERROR] Attempted to start session with null user.");
            return false;
        }

        boolean savedToDb = authRepository.saveSession(user.getId(), user.getUserType());

        if (savedToDb) {
            this.currentUser = user;
            System.out.println("[INFO] Session started for user: " + user.getName());
            return true;
        } else {
            System.err.println("[ERROR] Failed to save session to database for user: " + user.getName());
            this.currentUser = null;
            return false;
        }
    }

    /**
     * Ends the current session: deletes from DB and clears local user.
     * Should be called during logout.
     *
     * @return true if the session was deleted from DB (or no user was logged in), false if DB deletion failed.
     */
    public boolean endSession() {
        boolean deletedFromDb = true;
        if (this.currentUser != null) {
            System.out.println("[INFO] Ending session for user: " + currentUser.getName());
            deletedFromDb = authRepository.deleteSession(currentUser.getId());
            if (!deletedFromDb) {
                System.err.println("[ERROR] Failed to delete session from database for user ID: " + currentUser.getId());
            }
        } else {
            System.out.println("[INFO] No active session to end.");
        }

        this.currentUser = null;
        System.out.println("[INFO] Local session cleared.");
        return deletedFromDb;
    }

    /**
     * Retrieves the currently authenticated user in this session.
     *
     * @return The current AuthenticatedUser object if logged in, null otherwise.
     */
    public AuthenticatedUser getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks whether a user is currently logged in.
     *
     * @return true if a user session is active, false if no user is logged in.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}