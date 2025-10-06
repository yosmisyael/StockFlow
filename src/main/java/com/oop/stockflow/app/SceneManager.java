package com.oop.stockflow.app;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

public class SceneManager {
    public static Scene load(View view) {
        try {
            URL fxmlLocation = SceneManager.class.getResource(view.getFxmlFile());
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            return new Scene(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
