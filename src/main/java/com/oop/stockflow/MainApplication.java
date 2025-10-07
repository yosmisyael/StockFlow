package com.oop.stockflow;

import com.oop.stockflow.app.SceneManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.db.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseManager.getConnection();
        StageManager stageManager = StageManager.getInstance();
        stageManager.init(stage);
        Scene loginScene = SceneManager.load(View.LOGIN);
        stageManager.setScene(loginScene, "Login");
    }
}
