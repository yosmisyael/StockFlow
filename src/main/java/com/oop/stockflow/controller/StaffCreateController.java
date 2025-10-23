package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.StaffRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class StaffCreateController {
    private Warehouse currentWarehouse;
    private AuthenticatedUser currentUser;

    private final StaffRepository staffRepository = StaffRepository.getInstance();

    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label initialLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void goToWarehouseDashboard() {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_SHOW,
                "Dashboard",
                (WarehouseShowController controller) -> controller.initData(currentWarehouse, currentUser)
        );
    }

    @FXML
    private void goToStaffMenu() {
        StageManager.getInstance().navigateWithData(
            View.STAFF_INDEX,
            "Staff Management",
            (StaffIndexController controller) -> { controller.initData(currentWarehouse, currentUser); }
        );
    }

    @FXML
    private void goToProductIndex() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        StageManager.getInstance().navigateWithData(
                View.PRODUCT_INDEX,
                "Warehouse " + currentWarehouse.getId() + " Product Management",
                (ProductIndexController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    @FXML
    private void goToWarehouseEdit() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_EDIT,
                "Manage Warehouse " + currentWarehouse.getName(),
                (WarehouseEditController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    @FXML
    private void handleCancel() {
        goToStaffMenu();
    }

    @FXML
    private void handleCreateStaff() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // input not empty validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "All fields are required. Please fill them all.");
            return;
        }

        // email validation
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!Pattern.matches(emailRegex, email)) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Email address format is invalid. Please enter a valid email (e.g., example@domain.com).");
            return;
        }

        // password validation
        if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Password is too short. It must be at least 8 characters long.");
            return;
        }

        // check warehouse id exists
        if (currentWarehouse.getId() <= 0) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Warehouse ID is missing. Please go back and try again.");
            return;
        }

        boolean success = staffRepository.createStaff(name, email, password, this.currentWarehouse.getId());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "A new staff account has been created successfully!");
            goToStaffMenu();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create staff account. The username might already exist.");
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        currentUser = user;
        this.currentWarehouse = warehouse;
        loadPageContext();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // helper methods
    private void loadPageContext() {
        nameLabel.setText(currentUser.getName());
        roleLabel.setText(currentUser.getUserType().getDbValue());
        dateLabel.setText(DateTimeUtils.getCurrentDate());
        initialLabel.setText(StringUtils.getInitial(currentUser.getName()));
    }
}
