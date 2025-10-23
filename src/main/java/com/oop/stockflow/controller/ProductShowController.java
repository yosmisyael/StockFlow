package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.*;
import com.oop.stockflow.repository.ProductRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductShowController {
    private Warehouse currentWarehouse;
    private AuthenticatedUser currentUser;
    private Product productToShow;
private final ProductRepository productRepository = ProductRepository.getInstance();

    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label initialLabel;

    // Basic Information Card
    @FXML private Label skuLabel;
    @FXML private Label productTypeLabel;
    @FXML private Label productNameLabel;
    @FXML private Label brandLabel;
    @FXML private Label descriptionLabel;

    // Inventory Details Card
    @FXML private Label currentQuantityLabel;
    @FXML private Label reorderPointLabel;
    @FXML private Label reorderQuantityLabel;
    @FXML private Label unitsPerCaseLabel;
    @FXML private Label purchasePriceLabel;

    // Physical Specifications Card
    @FXML private Label weightLabel;
    @FXML private Label volumeLabel;

    // Storage & Alerts Card
    @FXML private Label temperatureLabel;
    @FXML private Label expiryAlertLabel;

    // Back Button
    @FXML private Button backButton;

    /**
     * Public method to receive context data after initialization.
     */
    public void initData(Warehouse warehouse, AuthenticatedUser user, Product product) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;
        this.productToShow = product;

        loadPageContext();

        if (this.currentUser == null || this.productToShow == null || this.currentWarehouse == null) {
            System.err.println("Error: User, Product, and Warehouse data are required for viewing product details.");
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Cannot load product details.");
            handleBack(null);
            return;
        }

        populateView();
    }

    /**
     * Fills the FXML Labels with data from productToShow.
     */
    private void populateView() {
        if (productToShow == null) return;
        // --- Formatters ---
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        DecimalFormat weightFormat = new DecimalFormat("#,##0.### 'kg'");
        DecimalFormat volumeFormat = new DecimalFormat("#,##0.### 'm³'");
        DecimalFormat tempFormat = new DecimalFormat("#,##0.0 '°C'");

        // --- Basic Information ---
        skuLabel.setText(String.valueOf(productToShow.getSku()));
        productTypeLabel.setText(StringUtils.toTitleCase(productToShow.getProductType().getDbValue()));
        productNameLabel.setText(productToShow.getName());
        brandLabel.setText(productToShow.getBrand() != null ? productToShow.getBrand() : "N/A");
        descriptionLabel.setText(productToShow.getDescription() != null ? productToShow.getDescription() : "N/A");

        // --- Inventory Details ---
        currentQuantityLabel.setText(productToShow.getQuantity() + " units");
        purchasePriceLabel.setText(productToShow.getPurchasePrice() != null ? currencyFormat.format(productToShow.getPurchasePrice()) : "N/A");

        // --- Physical Specifications ---
        weightLabel.setText(weightFormat.format(productToShow.getWeightPerUnitKg()));
        volumeLabel.setText(volumeFormat.format(productToShow.getVolumePerUnitM3()));

        if (productToShow instanceof DryGoodProduct dryGood) {
            // Populate Dry Good fields
            reorderPointLabel.setText(dryGood.getReorderPoint() + " units");
            reorderQuantityLabel.setText(dryGood.getReorderQuantity() + " units");
            unitsPerCaseLabel.setText(dryGood.getUnitsPerCase() + " units");

            // Hide or indicate N/A for Fresh fields
            temperatureLabel.setText("N/A (Dry Good)");
            temperatureLabel.getStyleClass().add("na-badge");
            expiryAlertLabel.setText("N/A (Dry Good)");
            expiryAlertLabel.getStyleClass().add("na-badge");

        } else if (productToShow instanceof FreshProduct fresh) {
            temperatureLabel.setText(fresh.getRequiredTemp() != null ? tempFormat.format(fresh.getRequiredTemp()) : "N/A");

            expiryAlertLabel.setText(fresh.getDaysToAlertBeforeExpiry() > 0 ? fresh.getDaysToAlertBeforeExpiry() + " days" : "N/A");
            if (fresh.getDaysToAlertBeforeExpiry() > 0) {
                expiryAlertLabel.getStyleClass().add("warning-badge");
            } else {
                expiryAlertLabel.getStyleClass().add("na-badge");
            }

            reorderPointLabel.setText("N/A (Fresh)");
            reorderQuantityLabel.setText("N/A (Fresh)");
            unitsPerCaseLabel.setText("N/A (Fresh)");
            reorderPointLabel.getStyleClass().add("na-badge");
            reorderQuantityLabel.getStyleClass().add("na-badge");
            unitsPerCaseLabel.getStyleClass().add("na-badge");
        }
    }


    // action handlers

    /**
     * Handles the "Back" button click. Navigates back to the product list.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        System.out.println("Back button clicked.");
        goToProductIndex();
    }

    // navigations
    @FXML
    private void goToWarehouseDashboard() {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_SHOW,
                "Dashboard",
                (WarehouseShowController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    @FXML
    private void goToStaffMenu() {
        StageManager.getInstance().navigateWithData(
                View.STAFF_INDEX,
                "Staff Management",
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
    private void handleLogout(ActionEvent event) {
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    // helper methods
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadPageContext() {
        nameLabel.setText(currentUser.getName());
        roleLabel.setText(currentUser.getUserType().getDbValue());
        dateLabel.setText(DateTimeUtils.getCurrentDate());
        initialLabel.setText(StringUtils.getInitial(currentUser.getName()));
    }
}