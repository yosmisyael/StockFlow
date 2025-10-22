package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.UserType;
import com.oop.stockflow.model.WarehouseStatus;
import com.oop.stockflow.repository.WarehouseRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class WarehouseCreateController {

    @FXML private TextField warehouseNameField;
    @FXML private TextArea addressField;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField postalCodeField;
    @FXML private TextField storageCapacityKgField;
    @FXML private TextField storageCapacityM3Field;

    @FXML private RadioButton activeStatus;
    @FXML private RadioButton inactiveStatus;
    @FXML private RadioButton maintenanceStatus;
    @FXML private ToggleGroup statusToggleGroup;


    private final WarehouseRepository warehouseRepository = WarehouseRepository.getInstance();

    @FXML
    private void initialize() {
        activeStatus.setUserData(WarehouseStatus.ACTIVE);
        inactiveStatus.setUserData(WarehouseStatus.INACTIVE);
        maintenanceStatus.setUserData(WarehouseStatus.MAINTENANCE);
    }

    @FXML
    private void onRegisterWarehouse() {
        String name = warehouseNameField.getText().trim();
        String address = addressField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String postalCode = postalCodeField.getText().trim();
        int capacityKg = parseIntSafe(storageCapacityKgField.getText());
        int capacityM3 = parseIntSafe(storageCapacityM3Field.getText());
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
            StageManager.getInstance().navigate(View.WAREHOUSE_INDEX, "Warehouse List");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to register warehouse.");
        }
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
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
        StageManager.getInstance().navigate(View.WAREHOUSE_INDEX, "Warehouse List");
    }

}
