package com.oop.stockflow.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class StageManager {
    private static StageManager instance;
    private Stage mainStage;

    public void setScene(Scene scene, String title) {
        if (this.mainStage == null) {
            throw new IllegalStateException("[ERROR] StageManager is not initialized.");
        }

        this.mainStage.setTitle(title);
        this.mainStage.setScene(scene);
    }

    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    public void init(Stage stage) {
        if (this.mainStage == null) {
            this.mainStage = stage;
            this.mainStage.setMinHeight(800);
            this.mainStage.setMinWidth(1200);
            this.mainStage.setResizable(true);
            this.mainStage.setMaximized(true);
        }
    }

    public Stage getMainStage() {
        return this.mainStage;
    }

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
