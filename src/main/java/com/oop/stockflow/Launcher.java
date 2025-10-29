package com.oop.stockflow;

import javafx.application.Application;

/**
 * The application launcher class.
 *
 * This class contains the standard main method required to launch a Java application.
 * Its sole responsibility is to call the JavaFX launch method, passing the main
 * application class (App) to begin the JavaFX lifecycle.
 */
public class Launcher {

    /**
     * The entry point for the entire application.
     *
     * This method initializes and starts the JavaFX runtime environment
     * by calling Application.launch() with the primary application class.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
