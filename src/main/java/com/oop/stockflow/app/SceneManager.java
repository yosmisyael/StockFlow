package com.oop.stockflow.app;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public class SceneManager {
    public static FXMLLoader getLoader(View view) throws IOException {
        URL fxmlLocation = SceneManager.class.getResource(view.getFxmlFile());
        if (fxmlLocation == null) {
            throw new IOException("Cannot find FXML file: " + view.getFxmlFile());
        }
        return new FXMLLoader(fxmlLocation);
    }

    public static Parent loadFxml(View view) {
        try {
            return getLoader(view).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
