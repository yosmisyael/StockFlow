package com.oop.stockflow.app;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class StageManager {
    private static StageManager instance;
    private Stage mainStage;

    public void setScene(Scene scene, String title) {
        if (this.mainStage == null) {
            throw new IllegalStateException("[ERROR] StageManager is not initialized.");
        }

        this.mainStage.setTitle(title);
        this.mainStage.setScene(scene);
        this.mainStage.centerOnScreen();
        this.mainStage.setMinWidth(1280);
        this.mainStage.setMinHeight(800);
        this.mainStage.setResizable(true);
        this.mainStage.show();
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
        }
    }

    public Stage getMainStage() {
        return this.mainStage;
    }
}
