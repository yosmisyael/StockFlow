package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.model.WarehouseStatus;
import com.oop.stockflow.repository.WarehouseRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class WarehouseEditController implements Initializable {
    private Warehouse currentWarehouse;
    private AuthenticatedUser currentUser;
    private final WarehouseRepository warehouseRepository = WarehouseRepository.getInstance();

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
    private TextField warehouseIdField;
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
    private ToggleGroup statusToggleGroup;
    @FXML
    private RadioButton activeStatus;
    @FXML
    private RadioButton inactiveStatus;
    @FXML
    private RadioButton maintenanceStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activeStatus.setUserData(WarehouseStatus.ACTIVE);
        inactiveStatus.setUserData(WarehouseStatus.INACTIVE);
        maintenanceStatus.setUserData(WarehouseStatus.MAINTENANCE);
        addDecimalValidationListener(storageCapacityKgField);
        addDecimalValidationListener(storageCapacityM3Field);
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
    }

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;

        if (currentWarehouse == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No warehouse data provided.");
            handleCancel(null);
            return;
        }
        loadPageContext();
        populateForm();
    }

    // navigations
    @FXML
    private void goToWarehouseList() {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_INDEX,
                "Warehouse List",
                (WarehouseIndexController controller) -> {
                    controller.initData(currentUser);
                }
        );
    }

    @FXML
    private void goToWarehouseDashboard() {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_SHOW,
                "Warehouse " + currentWarehouse.getId(),
                (WarehouseShowController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    @FXML
    private void goToStaffMenu() {
        StageManager.getInstance().navigateWithData(
                View.STAFF_INDEX,
                "Warehouse " + currentWarehouse.getId() + " Staff Management",
                (StaffIndexController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
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

    // action handlers
    @FXML
    private void handleLogout() {
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    @FXML
    private void handleUpdateWarehouse(ActionEvent event) {
        String name = warehouseNameField.getText().trim();
        String address = addressField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String postalCode = postalCodeField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() || postalCode.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return;
        }

        WarehouseStatus selectedStatus = getSelectedStatus();
        if (selectedStatus == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a warehouse status.");
            return;
        }

        double capacityKg = parseDoubleSafe(storageCapacityKgField.getText());
        double capacityM3 =  parseDoubleSafe(storageCapacityM3Field.getText());

        boolean success = warehouseRepository.updateWarehouse(
                currentWarehouse.getId(),
                name,
                address,
                city,
                state,
                postalCode,
                capacityKg,
                capacityM3,
                selectedStatus
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Warehouse details updated successfully!");
            currentWarehouse = warehouseRepository.getWarehouseById(currentWarehouse.getId());
            goToWarehouseEdit();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update warehouse.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_SHOW,
                "Warehouse Dashboard",
                (WarehouseShowController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    // helper methods
    private WarehouseStatus getSelectedStatus() {
        Toggle selected = statusToggleGroup.getSelectedToggle();
        if (selected != null) {
            return (WarehouseStatus) selected.getUserData();
        }
        return null;
    }

    private void loadPageContext() {
        nameLabel.setText(currentUser.getName());
        roleLabel.setText(StringUtils.toTitleCase(currentUser.getUserType().getDbValue()));
        dateLabel.setText(DateTimeUtils.getCurrentDate());
        initialLabel.setText(StringUtils.getInitial(currentUser.getName()));
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void populateForm() {
        warehouseNameField.setText(currentWarehouse.getName());
        warehouseIdField.setText(String.valueOf(currentWarehouse.getId()));
        addressField.setText(currentWarehouse.getAddress());
        cityField.setText(currentWarehouse.getCity());
        stateField.setText(currentWarehouse.getState());
        postalCodeField.setText(currentWarehouse.getPostalCode());
        storageCapacityKgField.setText(String.valueOf(currentWarehouse.getMaxCapacityWeight()));
        storageCapacityM3Field.setText(String.valueOf(currentWarehouse.getMaxCapacityVolume()));
        System.out.println("Max Capacity KG: " + currentWarehouse.getMaxCapacityWeight() + " Capcity volume: " + currentWarehouse.getMaxCapacityVolume());

        switch (currentWarehouse.getStatus()) {
            case ACTIVE:
                statusToggleGroup.selectToggle(activeStatus);
                break;
            case INACTIVE:
                statusToggleGroup.selectToggle(inactiveStatus);
                break;
            case MAINTENANCE:
                statusToggleGroup.selectToggle(maintenanceStatus);
                break;
        }
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