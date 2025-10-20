package com.oop.stockflow.app;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public class SceneManager {
    public static Parent loadFxml(View view) {
        try {
            URL fxmlLocation = SceneManager.class.getResource(view.getFxmlFile());
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
