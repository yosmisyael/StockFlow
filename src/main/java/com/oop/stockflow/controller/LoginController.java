package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Staff;
import com.oop.stockflow.model.UserType;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.AuthRepository;
import com.oop.stockflow.repository.StaffRepository;
import com.oop.stockflow.repository.WarehouseRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private final AuthRepository authRepo = AuthRepository.getInstance();

    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
        StageManager.getInstance().navigate(View.REGISTER, "Register");
    }

    @FXML
    private void handleLogin() throws IOException {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();


        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all fields!");
            return;
        }

        AuthenticatedUser user = authRepo.login(email, password);
        SessionManager.getInstance().startSession(user);

        if (user != null) {
            if (user.getUserType() ==  UserType.STAFF) {
                Staff staff = StaffRepository.getInstance().getStaffById(user.getId());
                Warehouse warehouse = WarehouseRepository.getInstance().getWarehouseById(staff.getWarehouseId());
                StageManager.getInstance().navigateWithData(
                        View.TRANSACTION_INDEX,
                        "Product Transactions",
                        (TransactionIndexController controller) -> { controller.initData(warehouse, user); }
                );
            } else {
                StageManager.getInstance().navigateWithData(
                        View.WAREHOUSE_INDEX,
                        "Warehouse List",
                        (WarehouseIndexController controller) -> { controller.initData(user); }
                );
            }
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