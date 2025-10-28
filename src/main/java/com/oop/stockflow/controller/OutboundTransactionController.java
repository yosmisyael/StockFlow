package com.oop.stockflow.controller;

import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.*;
import com.oop.stockflow.repository.ProductRepository;
import com.oop.stockflow.repository.TransactionRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class OutboundTransactionController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private ComboBox<Product> productSKUCombo;
    @FXML
    private TextField quantityField;
    @FXML
    private ComboBox<ShippingType> shippingMethodCombo;
    @FXML
    private DatePicker transactionDatePicker;
    @FXML
    private ComboBox<TransactionStatus> statusCombo;
    @FXML
    private TextArea destinationAddressArea;
    @FXML
    private Label dateLabel;
    @FXML
    private Label initialLabel;

    private final TransactionRepository transactionRepository = TransactionRepository.getInstance();
    private final ProductRepository productRepository = ProductRepository.getInstance();
    private AuthenticatedUser currentUser;
    private Warehouse currentWarehouse;

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;
        loadUserData();
        loadPageContext();
        populateComboBoxes();
        transactionDatePicker.setValue(LocalDate.now());
        addInputValidationListeners();
    }

    /**
     * Loads user data into the sidebar.
     */
    private void loadUserData() {
        nameLabel.setText(currentUser.getName());
        roleLabel.setText(currentUser.getUserType().getDbValue());
    }

    /**
     * Populates ComboBoxes (Product SKU, Shipping Method, Status) with data.
     */
    private void populateComboBoxes() {
         // populate products combo box
         List<Product> products = productRepository.getAllProductsByWarehouseId(currentWarehouse.getId());
         productSKUCombo.setItems(FXCollections.observableArrayList(products));
         productSKUCombo.setConverter(new StringConverter<Product>() {
             @Override
             public String toString(Product product) {
                 return (product == null) ? null  :  product.getName() + " (SKU: " + product.getSku() + ")";
             }

             @Override
             public Product fromString(String s) {
                 return null;
             }
         });

         // populate shipping method combo box
        shippingMethodCombo.setItems(FXCollections.observableArrayList(ShippingType.values()));
        shippingMethodCombo.getSelectionModel().selectFirst();
         // populate status combo box
        statusCombo.setItems(FXCollections.observableArrayList(TransactionStatus.values()));
        statusCombo.getSelectionModel().select(TransactionStatus.PENDING);
    }

    /**
     * Adds listeners for input validation (e.g., numeric quantity).
     */
    private void addInputValidationListeners() {
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantityField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    // === Action Handlers ===

    /**
     * Handles the "Create Transaction" button click.
     * Gathers form data, validates it, and calls the repository to save.
     */
    @FXML
    private void handleCreateTransaction(ActionEvent event) {
        if (currentWarehouse.getStatus() != WarehouseStatus.ACTIVE) {
            showAlert(Alert.AlertType.WARNING, "Prohibited Action", "You are not allowed to perform any transaction on non active warehouse");
            return;
        }

        Product selectedProduct = productSKUCombo.getValue();
        String quantityStr = quantityField.getText();
        ShippingType selectedShippingMethod = shippingMethodCombo.getValue();
        LocalDate selectedDate = transactionDatePicker.getValue();
        TransactionStatus selectedStatus = statusCombo.getValue();
        String destinationAddress = destinationAddressArea.getText().trim();

        // validations
        if (selectedProduct == null || quantityStr.isEmpty() || selectedShippingMethod == null || selectedDate == null || selectedStatus == null || destinationAddress.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all required fields.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Quantity must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Quantity must be a valid number.");
            return;
        }

         // stock validation
         boolean stockAvailable = checkStockAvailability(selectedProduct.getQuantity(), quantity);
         if (!stockAvailable) {
             showAlert(Alert.AlertType.ERROR, "Stock Error", "Insufficient stock for product: " + selectedProduct.getName());
             return;
         }

        // Assuming current user and warehouse ID are available
        Timestamp timestamp = Timestamp.valueOf(selectedDate.atStartOfDay());
        boolean success = transactionRepository.createOutboundTransaction(
             currentUser.getId(),
             timestamp,
             destinationAddress,
             selectedShippingMethod,
             quantity,
             selectedProduct.getSku(),
             selectedStatus
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Outbound transaction created successfully!");
            clearForm();
            goToTransactionIndex();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create outbound transaction.");
        }
    }

    /**
     * Handles the "Cancel" button click.
     * Navigates back to the transactions list view.
     */
    @FXML
    private void handleCancel() {
        StageManager.getInstance().navigateWithData(
                View.TRANSACTION_INDEX,
                "Product Transactions",
                (TransactionIndexController controller) -> controller.initData(currentWarehouse, currentUser)
        );
    }

    // navigations
    @FXML
    private void goToTransactionIndex() {
        StageManager.getInstance().navigateWithData(
                View.TRANSACTION_INDEX,
                "Product Transactions",
                (TransactionIndexController controller) -> controller.initData(currentWarehouse, currentUser)
        );
    }

    @FXML
    private void goToInboundTransaction() {
        StageManager.getInstance().navigateWithData(
                View.TRANSACTION_CREATE_INBOUND,
                "New Inbound Transaction",
                (InboundTransactionsController controller) -> controller.initData(currentWarehouse, currentUser)
        );
    }

    @FXML
    private void goToOutboundTransaction() {
        StageManager.getInstance().navigateWithData(
                View.TRANSACTION_CREATE_OUTBOUND,
                "New Outbound Transaction",
                (OutboundTransactionController controller) -> controller.initData(currentWarehouse, currentUser)
        );
    }

    @FXML
    private void goToSettings() {
        StageManager.getInstance().navigateWithData(
                View.STAFF_SETTINGS,
                "Settings",
                (StaffSettingsController controller) -> controller.initData(currentWarehouse, currentUser)
        );
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logging out...");
        // SessionManager.getInstance().endSession(); // Clear session if using one
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    // helper methods
    private boolean checkStockAvailability(int availableStock, int demand) {
        return availableStock >= demand;
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearForm() {
        productSKUCombo.getSelectionModel().clearSelection();
        quantityField.clear();
        shippingMethodCombo.getSelectionModel().clearSelection();
        transactionDatePicker.setValue(LocalDate.now()); // Reset to today
        statusCombo.getSelectionModel().select(TransactionStatus.PENDING); // Reset to default
        destinationAddressArea.clear();
    }

    /**
     * Shows a standard JavaFX Alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadPageContext() {
        dateLabel.setText(DateTimeUtils.getCurrentDate());
        initialLabel.setText(StringUtils.getInitial(currentUser.getName()));
    }
}