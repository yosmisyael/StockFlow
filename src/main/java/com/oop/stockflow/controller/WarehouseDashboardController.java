package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View; // Pastikan View.STOCK_MANAGEMENT sudah ada di sini
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Product;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.ProductRepository;
import com.oop.stockflow.repository.TransactionRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WarehouseDashboardController {
    private ProductRepository productRepository = ProductRepository.getInstance();
    private TransactionRepository transactionRepository = TransactionRepository.getInstance();

    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label initialLabel;


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
    private Label inboundTodayLabel;
    @FXML
    private Label lowStockLabel;

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
        loadPageContext();
        updateUI();
    }

    private void updateUI() {
        if (currentWarehouse != null) {
            warehouseName.setText(currentWarehouse.getName());
            warehouseAddress.setText(currentWarehouse.getAddress());
            loadStats();
        }
    }

    private void loadStats() {
        // count inbound today
        int countInboundToday = 0;
        countInboundToday = transactionRepository.countTodayInboundTransaction();
        inboundTodayLabel.setText(String.valueOf(countInboundToday));

        // count outbound today
        int countOutboundToday = 0;
        countOutboundToday = transactionRepository.countTodayOutboundTransaction();
        outboundTodayLabel.setText(String.valueOf(countOutboundToday));

        // count low stock
        int countLowStock = 0;
        countLowStock = productRepository.countLowStockByWarehouseId(currentWarehouse.getId());
        lowStockLabel.setText(String.valueOf(countLowStock));

        // calculate stock
        int countStock = 0;
        countStock = productRepository.countProductsByWarehouseId(currentWarehouse.getId());
        totalStockLabel.setText(String.valueOf(countStock));
    }

    @FXML
    private void goToWarehouseList() {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_INDEX,
                "Warehouse List",
                (WarehouseController controller) -> {
                    controller.initData(currentUser);
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

    // action handlers
    @FXML
    private void handleLogout() {
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    // helper methods
    private void loadPageContext() {
        nameLabel.setText(currentUser.getName());
        roleLabel.setText(currentUser.getUserType().getDbValue());
        dateLabel.setText(DateTimeUtils.getCurrentDate());
        initialLabel.setText(StringUtils.getInitial(currentUser.getName()));
    }
}