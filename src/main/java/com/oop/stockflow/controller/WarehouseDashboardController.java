package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View; // Pastikan View.STOCK_MANAGEMENT sudah ada di sini
import com.oop.stockflow.model.AuthenticatedUser;
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
    private Label totalStockCardLabel;
    @FXML
    private Label outboundTodayCardLabel;
    @FXML
    private Label inboundTodayCardLabel;
    @FXML
    private Label lowStockCardLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label inStockLabel;
    @FXML
    private Label outStockLabel;


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
        inboundTodayCardLabel.setText(String.valueOf(countInboundToday));

        // count outbound today
        int countOutboundToday = 0;
        countOutboundToday = transactionRepository.countTodayOutboundTransaction();
        outboundTodayCardLabel.setText(String.valueOf(countOutboundToday));

        // count low stock product
        int countLowStock = 0;
        countLowStock = productRepository.countLowStockByWarehouseId(currentWarehouse.getId());
        lowStockCardLabel.setText(String.valueOf(countLowStock));
        lowStockLabel.setText(String.valueOf(countLowStock));

        // count in stock product
        int countInStock = 0;
        countInStock = productRepository.countInStock(currentWarehouse.getId());
        inStockLabel.setText(String.valueOf(countInStock));

        // count out stock product
        int countOutStock = 0;
        countOutStock = productRepository.countOutStock(currentWarehouse.getId());
        outStockLabel.setText(String.valueOf(countOutStock));

        // calculate stock
        int countStock = 0;
        countStock = productRepository.countProductsByWarehouseId(currentWarehouse.getId());
        totalStockCardLabel.setText(String.valueOf(countStock));
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