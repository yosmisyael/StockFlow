package com.oop.stockflow.controller;

import com.oop.stockflow.repository.WarehouseRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class WarehouseCreateController {

    @FXML private TextField warehouseNameField;
    @FXML private TextField warehouseIdField;
    @FXML private TextArea addressField;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField postalCodeField;
    @FXML private TextField storageCapacityKgField;
    @FXML private TextField storageCapacityM3Field;
    @FXML private TextField numberOfStaffField;

    @FXML private RadioButton activeStatus;
    @FXML private RadioButton inactiveStatus;
    @FXML private RadioButton maintenanceStatus;

    private final WarehouseRepository warehouseRepository = new WarehouseRepository();

    @FXML
    private void initialize() {
        // Optional initialization logic if needed
    }

    @FXML
    private void onRegisterWarehouse() {
        String name = warehouseNameField.getText().trim();
        String id = warehouseIdField.getText().trim();
        String address = addressField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String postalCode = postalCodeField.getText().trim();

        int capacityKg = parseIntSafe(storageCapacityKgField.getText());
        int capacityM3 = parseIntSafe(storageCapacityM3Field.getText());

        String status;
        if (activeStatus.isSelected()) status = "Active";
        else if (inactiveStatus.isSelected()) status = "Inactive";
        else status = "Maintenance";

        if (name.isEmpty() || id.isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return;
        }

        boolean success = warehouseRepository.insertWarehouse(name, address, capacityKg, capacityM3, 1);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Warehouse registered successfully!");
            clearForm();
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
        warehouseIdField.clear();
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
}
