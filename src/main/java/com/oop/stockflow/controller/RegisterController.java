package com.oop.stockflow.controller;

import com.oop.stockflow.app.SceneManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.repository.ManagerRepository;
import com.oop.stockflow.utils.PasswordUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
    @FXML private TextField emailField;
    @FXML private TextField nameField;
    @FXML private TextField companyNameField;
    @FXML private PasswordField passwordField;

    private final ManagerRepository managerRepo = new ManagerRepository();

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        StageManager.getInstance().navigateTo(View.LOGIN, "Login");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        String company = companyNameField.getText().trim();
        String password = passwordField.getText().trim();

        // basic validations
        if (email.isEmpty() || name.isEmpty() || company.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all fields!");
            return;
        }

        // email validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert(Alert.AlertType.WARNING, "Please enter a valid email address.");
            return;
        }

        // password strength check
        if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Password must be at least 8 characters long.");
            return;
        }

        // name sanity checks
//        if (!name.matches("^[A-Za-z ]+$")) {
//            showAlert(Alert.AlertType.WARNING, "Name can only contain letters and spaces.");
//            return;
//        }

        // company sanity checks
//        if (company.length() < 2) {
//            showAlert(Alert.AlertType.WARNING, "Company name is too short.");
//            return;
//        }

        String hashedPassword = PasswordUtils.hashPassword(password);

        boolean success = managerRepo.registerManager(name, email, company, hashedPassword);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Registration successful! Now, please login with your account.");
            StageManager.getInstance().navigateTo(View.LOGIN, "Login");
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to register. Try again.");
        }

    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
