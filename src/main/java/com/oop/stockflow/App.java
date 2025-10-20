package com.oop.stockflow;

import com.oop.stockflow.app.SceneManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
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
