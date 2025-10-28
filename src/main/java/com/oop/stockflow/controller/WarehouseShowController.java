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
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class WarehouseShowController {
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

    // charts and notifications
    @FXML
    private LineChart<String, Number> outboundChart;
    @FXML
    private VBox notificationsContainer;

    private Warehouse currentWarehouse;
    private AuthenticatedUser currentUser;

    private void loadStats() {
        // count inbound today
        int countInboundToday = 0;
        countInboundToday = transactionRepository.countTodayInboundTransaction(currentWarehouse.getId());
        inboundTodayCardLabel.setText(String.valueOf(countInboundToday));

        // count outbound today
        int countOutboundToday = 0;
        countOutboundToday = transactionRepository.countTodayOutboundTransaction(currentWarehouse.getId());
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

    @FXML
    private void goToProductCreate() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        StageManager.getInstance().navigateWithData(
                View.PRODUCT_CREATE,
                "Add Product " + currentWarehouse.getName(),
                (ProductCreateController controller) -> {
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

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        currentUser = user;
        currentWarehouse = warehouse;
        loadPageContext();
        updateUI();
    }

    /**
     * Helper method to draw the outbound transaction chart.
     * Assuming the chart shows outbound counts over the last 7 days.
     */
    private void drawOutboundChart() {
        outboundChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Outbound Transactions");

        final int DAYS_TO_SHOW = 7;
        Map<LocalDate, Integer> counts = transactionRepository.getOutboundTransactionCounts(
                currentWarehouse.getId(),
                DAYS_TO_SHOW
        );

        DateTimeFormatter chartLabelFormatter = DateTimeFormatter.ofPattern("M/d");
        LocalDate today = LocalDate.now();

        for (Map.Entry<LocalDate, Integer> entry : counts.entrySet()) {
            LocalDate date = entry.getKey();
            int count = entry.getValue();

            String label = date.equals(today) ? "Today" : date.format(chartLabelFormatter);

            series.getData().add(new XYChart.Data<>(label, count));
        }

        outboundChart.getData().add(series);

        outboundChart.getXAxis().setLabel("Day");
        outboundChart.getYAxis().setLabel("Count");
    }

    private void updateUI() {
        if (currentWarehouse != null) {
            warehouseName.setText(currentWarehouse.getName());
            warehouseAddress.setText(currentWarehouse.getAddress());
            loadStats();
            drawOutboundChart();
        }
    }
}