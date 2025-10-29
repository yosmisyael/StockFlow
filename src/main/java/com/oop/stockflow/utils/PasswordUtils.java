package com.oop.stockflow.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for secure password handling using the jBCrypt library.
 *
 * This class provides methods for hashing (salting and securing) a plain-text
 * password and for verifying a plain-text password against a stored hash.
 */
public class PasswordUtils {
    /**
     * Hashes a plain-text password using BCrypt with a cost factor of 12.
     * A random salt is automatically generated and included in the resulting hash.
     *
     * @param plainPassword The password string to be securely hashed.
     * @return The securely hashed (salted) password string.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Verifies if a plain-text password matches a previously hashed password.
     * This method correctly extracts the salt from the hashed password for comparison.
     *
     * @param plainPassword The plain-text password provided by the user (e.g., during login).
     * @param hashedPassword The stored hashed password to compare against.
     * @return true if the plain password matches the hashed password, false otherwise.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
