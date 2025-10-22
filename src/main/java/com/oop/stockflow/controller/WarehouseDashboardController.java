package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View; // Pastikan View.STOCK_MANAGEMENT sudah ada di sini
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Warehouse;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class WarehouseDashboardController {
    // Sidebar User Profile
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRole;

    // Main Content Header
    @FXML
    private Label warehouseName;
    @FXML
    private Label warehouseAddress;

    // Stats Cards
    @FXML
    private Label totalStockLabel;
    @FXML
    private Label outboundTodayLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label expiringSoonLabel;

    // Charts & Notifications
    @FXML
    private LineChart<?, ?> outboundChart;
    @FXML
    private VBox notificationsContainer;

    private Warehouse currentWarehouse;
    private AuthenticatedUser currentUser;

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        currentUser = user;
        currentWarehouse = warehouse;
        updateUI();
    }

    private void updateUI() {
        if (currentWarehouse != null) {
            warehouseName.setText(currentWarehouse.getName());
            warehouseAddress.setText(currentWarehouse.getAddress());
        }
    }

    @FXML
    private void goToWarehouseList() {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_INDEX,
                "Warehouse List",
                (WarehouseController controller) -> { controller.initData(currentUser); }
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
}