package com.oop.stockflow.controller;

import com.oop.stockflow.app.SceneManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;

import java.io.IOException;

public class RegisterController {
    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        Scene registerScene = SceneManager.load(View.LOGIN);
        StageManager.getInstance().setScene(registerScene, "Login");
    }
}
