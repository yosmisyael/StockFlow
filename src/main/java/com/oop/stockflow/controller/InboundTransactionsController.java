package com.oop.stockflow.controller;

import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.*;
import com.oop.stockflow.repository.ProductRepository;
import com.oop.stockflow.repository.TransactionRepository;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Timestamp;
import java.time.LocalDate;

public class InboundTransactionsController {
    // sidebar fields
    @FXML private Label nameLabel;
    @FXML private Label roleLabel;

    @FXML private Button btnTransactionsList;
    @FXML private Button btnInbound;
    @FXML private Button btnOutbound;
    @FXML private Button btnSettings;

    @FXML private ComboBox<Product> cmbProductSku;
    @FXML private TextField txtQuantity;
    @FXML private ComboBox<ShippingType> cmbShippingMethod;
    @FXML private DatePicker dateTransaction;
    @FXML private ComboBox<TransactionStatus> cmbStatus;
    @FXML private Button btnCreate;
    @FXML private Button btnCancel;

    private AuthenticatedUser currentUser;
    private Warehouse currentWarehouse;
    private final TransactionRepository transactionRepository = TransactionRepository.getInstance();
    private final ProductRepository productRepository = ProductRepository.getInstance();

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        currentUser = user;
        currentWarehouse = warehouse;
        loadUserData();
        populateComboBoxes();
        dateTransaction.setValue(LocalDate.now());
        cmbStatus.getSelectionModel().select(TransactionStatus.PENDING);
        addInputValidationListeners();
    }

    private void loadUserData() {
        nameLabel.setText(currentUser.getName());
    }


    /**
     * Populates ComboBoxes with data (Products, Shipping Types, Statuses).
     */
    private void populateComboBoxes() {
        // TODO: Replace with actual data fetching from ProductRepository
        // List<Product> products = productRepository.getAllProducts();
        // cmbProductSku.setItems(FXCollections.observableArrayList(products));
        // TODO: Add a StringConverter to display Product name/SKU nicely
        // cmbProductSku.setConverter(new StringConverter<Product>() { ... });
        cmbProductSku.setItems(FXCollections.observableArrayList(/* Sample Products or fetch */));


        // --- Shipping Method ---
        cmbShippingMethod.setItems(FXCollections.observableArrayList(ShippingType.values()));
        cmbShippingMethod.getSelectionModel().selectFirst(); // Select a default

        // --- Status ---
        cmbStatus.setItems(FXCollections.observableArrayList(TransactionStatus.values()));
        cmbStatus.getSelectionModel().select(TransactionStatus.PENDING); // Default is Pending
    }

    /**
     * Adds input validation listeners (e.g., numeric quantity).
     */
    private void addInputValidationListeners() {
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) { // Only allow digits
                txtQuantity.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }


    // === Action Handlers ===

    /**
     * Handles the "+ Create Transaction" button click.
     * Validates input and calls the repository to save the inbound transaction.
     */
    @FXML
    private void createTransaction(ActionEvent event) {
        Product selectedProduct = cmbProductSku.getValue();
        String quantityStr = txtQuantity.getText();
        ShippingType selectedShipping = cmbShippingMethod.getValue();
        LocalDate selectedDate = dateTransaction.getValue();
        TransactionStatus selectedStatus = cmbStatus.getValue();

        if (selectedProduct == null || quantityStr.isEmpty() || selectedShipping == null || selectedDate == null || selectedStatus == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill all required fields.");
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

        // --- 3. Prepare Data for Repository ---
        // TODO: Get current staff ID and warehouse ID
        int staffId = 1; // Placeholder - Get from SessionManager
        // int warehouseId = 1; // Placeholder - Get from SessionManager or initData
        Timestamp timestamp = Timestamp.valueOf(selectedDate.atStartOfDay());
        int productSku = selectedProduct.getSku();

        // --- 4. Call Repository ---
        System.out.println("Creating Inbound Transaction:");
        System.out.println("  SKU: " + productSku + ", Qty: " + quantity + ", Date: " + timestamp + ", Ship: " + selectedShipping + ", Status: " + selectedStatus);

        boolean success = transactionRepository.createInboundTransaction(
                staffId,
                timestamp,
                selectedShipping,
                productSku,
                quantity,
                selectedStatus
        );

        // --- 5. Feedback & Cleanup ---
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Inbound transaction created successfully!");
            clearForm();
            // Optionally navigate back or refresh something
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create inbound transaction.");
            // Consider more specific error messages based on repo feedback
        }
    }

    /**
     * Handles the "Cancel" button click.
     * Clears the form or navigates away.
     */
    @FXML
    private void cancelTransaction(ActionEvent event) {
        System.out.println("Cancel button clicked.");
        clearForm();
        // Or navigate back to the transaction list:
        // navigateToTransactionsList(event);
    }


    // navigations
    @FXML
    private void goToTransactionIndex(ActionEvent event) {
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
        // SessionManager.getInstance().endSession(); // Clear session
        StageManager.getInstance().navigate(View.LOGIN, "Login"); // Navigate to Login
    }

    // === Helper Methods ===

    /**
     * Clears all input fields in the form.
     */
    private void clearForm() {
        cmbProductSku.getSelectionModel().clearSelection();
        txtQuantity.clear();
        cmbShippingMethod.getSelectionModel().selectFirst(); // Reset to default
        dateTransaction.setValue(LocalDate.now()); // Reset to today
        cmbStatus.getSelectionModel().select(TransactionStatus.PENDING); // Reset to default
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
}