package com.oop.stockflow.controller;

import com.oop.stockflow.app.SceneManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
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

        boolean isAuthenticated = authRepo.login(email, password);

        if (isAuthenticated) {
            showAlert(Alert.AlertType.INFORMATION, "Login successful!");

//            Scene dashboardScene = SceneManager.load(View.DASHBOARD);
//            StageManager.getInstance().setScene(dashboardScene, "Dashboard");
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