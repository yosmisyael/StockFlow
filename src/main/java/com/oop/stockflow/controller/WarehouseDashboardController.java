package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Warehouse;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class WarehouseDashboardController implements Initializable {
    // Sidebar User Profile
    @FXML private Label userNameLabel;
    @FXML private Label userRole;

    // Main Content Header
    @FXML private Label warehouseName;
    @FXML private Label warehouseAddress;

    // Stats Cards
    @FXML private Label totalStockLabel;
    @FXML private Label outboundTodayLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label expiringSoonLabel;

    // Charts & Notifications
    @FXML private LineChart<?, ?> outboundChart;
    @FXML private VBox notificationsContainer;

    private Warehouse warehouse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AuthenticatedUser user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getName());
            userRole.setText(user.getUserType().getDbValue());
        }
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
        updateUI();
    }

    private void updateUI() {
        if (warehouse != null) {
            warehouseName.setText(warehouse.getName());
            warehouseAddress.setText(warehouse.getAddress());
        }
    }

    @FXML
    private void goToWarehouseList() {
        StageManager.getInstance().navigate(View.WAREHOUSE_INDEX, "Warehouse List");
    }

    @FXML
    private void goToStaffMenu() {
        StageManager.getInstance().navigate(View.STAFF_INDEX, "Staff Management");
    }
}