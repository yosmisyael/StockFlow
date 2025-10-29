package com.oop.stockflow.app;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * Utility class for managing FXML file loading operations.
 * Provides static methods to load FXML files and create FXMLLoader instances
 * for JavaFX scene construction throughout the application.
 */
public class SceneManager {
    /**
     * Creates and returns an FXMLLoader for the specified view.
     * Locates the FXML file associated with the view and initializes a loader.
     *
     * @param view The View enum containing the FXML file path.
     * @return A configured FXMLLoader instance for the specified view.
     * @throws IOException If the FXML file cannot be found or accessed.
     */
    public static FXMLLoader getLoader(View view) throws IOException {
        URL fxmlLocation = SceneManager.class.getResource(view.getFxmlFile());
        if (fxmlLocation == null) {
            throw new IOException("Cannot find FXML file: " + view.getFxmlFile());
        }
        return new FXMLLoader(fxmlLocation);
    }

    /**
     * Loads and returns the Parent node from the specified view's FXML file.
     * This is a convenience method that handles the loading process automatically.
     *
     * @param view The View enum containing the FXML file to load.
     * @return The loaded Parent node representing the view's UI structure.
     * @throws RuntimeException If an IOException occurs during FXML loading.
     */
    public static Parent loadFxml(View view) {
        try {
            return getLoader(view).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
