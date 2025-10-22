package com.oop.stockflow.utils;

public class StringUtils {
    /**
     * Gets the initials of the first one or two words from a string.
     * Handles null, empty strings, and extra whitespace.
     *
     * @param fullString The input string (e.g., "John Doe", "Alice", " Peter Pan ").
     * @return The initials in uppercase (e.g., "JD", "A", "PP"), or an empty string
     * if the input is null or effectively empty after trimming.
     */
    public static String getInitial(String fullString) {
        if (fullString == null || fullString.trim().isEmpty()) {
            return "";
        }

        String trimmedName = fullString.trim();
        String[] words = trimmedName.split("\\s+");

        if (words.length == 0) {
            return "";
        } else if (words.length == 1) {
            return words[0].substring(0, 1).toUpperCase();
        } else {
            String firstInitial = words[0].substring(0, 1);
            String secondInitial = words[1].substring(0, 1);
            return (firstInitial + secondInitial).toUpperCase();
        }
    }
}
