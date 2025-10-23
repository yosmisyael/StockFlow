package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.UserType;
import com.oop.stockflow.model.WarehouseStatus;
import com.oop.stockflow.repository.WarehouseRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class WarehouseCreateController {
    // data
    AuthenticatedUser currentUser;

    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label initialLabel;
    @FXML
    private TextField warehouseNameField;
    @FXML
    private TextArea addressField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField stateField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private TextField storageCapacityKgField;
    @FXML
    private TextField storageCapacityM3Field;

    @FXML
    private RadioButton activeStatus;
    @FXML
    private RadioButton inactiveStatus;
    @FXML
    private RadioButton maintenanceStatus;
    @FXML
    private ToggleGroup statusToggleGroup;


    private final WarehouseRepository warehouseRepository = WarehouseRepository.getInstance();

    @FXML
    private void initialize() {
        activeStatus.setUserData(WarehouseStatus.ACTIVE);
        inactiveStatus.setUserData(WarehouseStatus.INACTIVE);
        maintenanceStatus.setUserData(WarehouseStatus.MAINTENANCE);
        postalCodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                return;
            }

            if (newValue.equals("0") && oldValue.isEmpty()) {
                Platform.runLater(() -> postalCodeField.clear());
                return;
            }

            if (!newValue.matches("\\d*")) {
                postalCodeField.setText(oldValue);
            }
        });
        addDecimalValidationListener(storageCapacityKgField);
        addDecimalValidationListener(storageCapacityM3Field);
    }

    public void initData(AuthenticatedUser user) {
        currentUser = user;
        loadPageContext();
    }

    // action handlers
    @FXML
    private void handleLogout() {
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    @FXML
    private void handleRegisterWarehouse() {
        String name = warehouseNameField.getText().trim();
        String address = addressField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String postalCode = postalCodeField.getText().trim();
        double capacityKg = parseDoubleSafe(storageCapacityKgField.getText());
        double capacityM3 = parseDoubleSafe(storageCapacityM3Field.getText());
        Toggle selectedToggle = statusToggleGroup.getSelectedToggle();

        if (name.isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return;
        }

        if (selectedToggle == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a warehouse status.");
            return;
        }

        WarehouseStatus status = (WarehouseStatus) selectedToggle.getUserData();

        AuthenticatedUser currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "No user is logged in. Please log in again.");
            return;
        }

        if (currentUser.getUserType() != UserType.MANAGER) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied", "Only managers can create new warehouses.");
            return;
        }

        boolean success = warehouseRepository.insertWarehouse(name, address, city, state, postalCode, capacityKg, capacityM3, status, currentUser.getId());
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Warehouse registered successfully!");
            clearForm();
            StageManager.getInstance().navigateWithData(
                    View.WAREHOUSE_INDEX,
                    "Warehouse List",
                    (WarehouseIndexController controller) -> { controller.initData(currentUser); }
            );
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to register warehouse.");
        }
    }

    private Double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void clearForm() {
        warehouseNameField.clear();
        addressField.clear();
        cityField.clear();
        stateField.clear();
        postalCodeField.clear();
        storageCapacityKgField.clear();
        storageCapacityM3Field.clear();
        activeStatus.setSelected(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goToWarehouseList(ActionEvent event) throws IOException {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_INDEX,
                "Warehouse List",
                (WarehouseIndexController controller) -> { controller.initData(currentUser); }
        );
    }

    // helper methods
    private void loadPageContext() {
        nameLabel.setText(currentUser.getName());
        roleLabel.setText(currentUser.getUserType().getDbValue());
        dateLabel.setText(DateTimeUtils.getCurrentDate());
        initialLabel.setText(StringUtils.getInitial(currentUser.getName()));
    }

    private void addDecimalValidationListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                return;
            }

            String validRegex = "^(?!0\\d)\\d*(\\.\\d*)?$";

            if (!newValue.matches(validRegex)) {
                textField.setText(oldValue);
            }
        });
    }
}
