package com.oop.stockflow;

import com.oop.stockflow.app.SceneManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main application class for StockFlow.
 *
 * This class extends JavaFX's Application and serves as the entry point
 * for the JavaFX GUI framework. It initializes the primary stage, sets up
 * the StageManager, and loads the initial application view (Login scene).
 */
public class App extends Application {

    /**
     * The primary entry point for the JavaFX application.
     * Called automatically after the application is launched by the main method in Launcher.
     *
     * @param stage The primary Stage (window) provided by the JavaFX system.
     * @throws IOException If the FXML file for the initial view (LOGIN) cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        StageManager stageManager = StageManager.getInstance();
        stageManager.init(stage);

        Parent root = SceneManager.loadFxml(View.LOGIN);
        Scene loginScene = new Scene(root);
        stageManager.setScene(loginScene, "Login");

        stage.show();
    }
}
