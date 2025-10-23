package com.oop.stockflow.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * Converts a string with space separators to Title Case.
     * Example: "dry good" becomes "Dry Good", "active" becomes "Active".
     * Handles null or empty input gracefully.
     *
     * @param inputStr The string from the database (e.g., "dry good").
     * @return The Title Case version of the string, or the original if null/empty.
     */
    public static String toTitleCase(String inputStr) {
        if (inputStr == null || inputStr.trim().isEmpty()) {
            return inputStr;
        }

        return Stream.of(inputStr.trim().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
