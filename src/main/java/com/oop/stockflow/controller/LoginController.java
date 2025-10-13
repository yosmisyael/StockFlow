package com.oop.stockflow.controller;

import com.oop.stockflow.app.SceneManager;
import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.repository.AuthRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private final AuthRepository authRepo = new AuthRepository();

    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
        Scene registerScene = SceneManager.load(View.REGISTER);
        StageManager.getInstance().setScene(registerScene, "Register");
    }

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all fields!");
            return;
        }

        AuthenticatedUser user = authRepo.login(email, password);

        if (user != null) {
            SessionManager.getInstance().startSession(user);

            Scene dashboardScene = SceneManager.load(View.WAREHOUSE_LIST);
            StageManager.getInstance().setScene(dashboardScene, "Warehouse List");
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid email or password!");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}