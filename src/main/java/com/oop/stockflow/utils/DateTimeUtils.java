package com.oop.stockflow.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utility class for common date and time operations, particularly focused
 * on formatting dates for display in the application.
 */
public class DateTimeUtils {

    /**
     * Retrieves the current system date and formats it into a specific readable string.
     * The format used is "DayOfWeek, dd/MM/yyyy" (e.g., "Wednesday, 29/10/2025").
     * The output uses English for the Day of Week name.
     *
     * @return A formatted String representing the current date.
     */
    public static String getCurrentDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.ENGLISH);
        return today.format(formatter);
    }
}
