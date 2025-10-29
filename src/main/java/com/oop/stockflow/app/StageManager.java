package com.oop.stockflow.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * Manages the primary JavaFX Stage and handles scene navigation throughout the application.
 * Implements singleton pattern to ensure only one stage manager exists.
 * Provides methods for switching between different views with or without passing data to controllers.
 */
public class StageManager {
    private static StageManager instance;
    private Stage mainStage;

    /**
     * Sets a new scene to the main stage with the specified title.
     * Updates both the scene content and window title.
     *
     * @param scene The Scene object to be displayed on the stage.
     * @param title The title to be shown in the window's title bar.
     * @throws IllegalStateException If StageManager has not been initialized.
     */
    public void setScene(Scene scene, String title) {
        if (this.mainStage == null) {
            throw new IllegalStateException("[ERROR] StageManager is not initialized.");
        }

        this.mainStage.setTitle(title);
        this.mainStage.setScene(scene);
    }

    /**
     * Returns the singleton instance of StageManager.
     * Creates a new instance if one doesn't exist yet (lazy initialization).
     *
     * @return The single StageManager instance used throughout the application.
     */
    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    /**
     * Initializes the StageManager with the primary stage.
     * Sets default properties: minimum size (1200x800), resizable, and maximized.
     * Should be called once during application startup.
     *
     * @param stage The primary Stage object from JavaFX Application.
     */
    public void init(Stage stage) {
        if (this.mainStage == null) {
            this.mainStage = stage;
            this.mainStage.setMinHeight(800);
            this.mainStage.setMinWidth(1200);
            this.mainStage.setResizable(true);
            this.mainStage.setMaximized(true);
        }
    }

    /**
     * Retrieves the main stage managed by this StageManager.
     *
     * @return The primary Stage object, or null if not yet initialized.
     */
    public Stage getMainStage() {
        return this.mainStage;
    }

    /**
     * Navigates to a new view by loading its FXML and displaying it on the main stage.
     * The new scene maintains the current stage dimensions.
     *
     * @param view The View enum representing the target FXML view to navigate to.
     * @param title The title to be displayed in the window's title bar.
     */
    public void navigate(View view, String title) {
        try {
            Parent root = SceneManager.loadFxml(view);

            if (this.mainStage == null) {
                throw new IllegalStateException("[ERROR] StageManager is not initialized.");
            }

            Scene scene = new Scene(root, this.mainStage.getWidth(), this.mainStage.getHeight());

            this.setScene(scene, title);

        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Navigates to a new view and passes data to its controller before displaying.
     * Allows pre-configuration of the controller through a Consumer callback.
     * The new scene maintains the current stage dimensions.
     *
     * @param <T> The type of the controller class.
     * @param view The View enum representing the target FXML view to navigate to.
     * @param title The title to be displayed in the window's title bar.
     * @param controllerConsumer A Consumer function to configure the controller with data before the scene is displayed.
     */
    public <T> void navigateWithData(View view, String title, Consumer<T> controllerConsumer) {
        try {
            FXMLLoader loader = SceneManager.getLoader(view);
            Parent root = loader.load();

            T controller = loader.getController();
            controllerConsumer.accept(controller);

            Scene scene = new Scene(root, this.mainStage.getWidth(), this.mainStage.getHeight());
            setScene(scene, title);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }
}
