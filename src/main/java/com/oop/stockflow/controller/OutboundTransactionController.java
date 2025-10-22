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
import java.time.LocalDate;

public class OutboundTransactionController {
    @FXML
    private Label nameLabel;
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

    private final TransactionRepository transactionRepository = TransactionRepository.getInstance();
    private final ProductRepository productRepository = ProductRepository.getInstance();
    private AuthenticatedUser currentUser;
    private Warehouse currentWarehouse;

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;
        loadUserData();
        populateComboBoxes();
        transactionDatePicker.setValue(LocalDate.now());
        addInputValidationListeners();
    }

    /**
     * Loads user data into the sidebar.
     */
    private void loadUserData() {
        nameLabel.setText(currentUser.getName());
    }

    /**
     * Populates ComboBoxes (Product SKU, Shipping Method, Status) with data.
     */
    private void populateComboBoxes() {
        // Example: Fetch products from ProductRepository
        // List<Product> products = productRepository.getAvailableProducts(currentWarehouseId); // Method needed
        // productSKUCombo.setItems(FXCollections.observableArrayList(products));
        // You'll need a way to display Product objects nicely (e.g., using setConverter)
        productSKUCombo.setItems(FXCollections.observableArrayList( /* Add sample Product objects or fetch real ones */));

        // --- Shipping Method ---
        shippingMethodCombo.setItems(FXCollections.observableArrayList(ShippingType.values()));
        // Optional: Set default selection
        // shippingMethodCombo.getSelectionModel().selectFirst();

        // --- Status ---
        statusCombo.setItems(FXCollections.observableArrayList(TransactionStatus.values()));
        // Set default status, e.g., PENDING
        statusCombo.getSelectionModel().select(TransactionStatus.PENDING);
    }

    /**
     * Adds listeners for input validation (e.g., numeric quantity).
     */
    private void addInputValidationListeners() {
        // Example: Ensure quantity is numeric
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) { // Allow only digits
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
        System.out.println("handleCreateTransaction called");

        // --- 1. Get Data from Form ---
        Product selectedProduct = productSKUCombo.getValue();
        String quantityStr = quantityField.getText();
        ShippingType selectedShippingMethod = shippingMethodCombo.getValue();
        LocalDate selectedDate = transactionDatePicker.getValue();
        TransactionStatus selectedStatus = statusCombo.getValue();
        String destinationAddress = destinationAddressArea.getText().trim();

        // --- 2. Basic Validation ---
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

        // --- 3. (Optional) More Advanced Validation ---
        // Check if stock is available for the selected product and quantity
        // boolean stockAvailable = checkStockAvailability(selectedProduct.getSku(), quantity);
        // if (!stockAvailable) {
        //     showAlert(Alert.AlertType.ERROR, "Stock Error", "Insufficient stock for product: " + selectedProduct.getName());
        //     return;
        // }


        // --- 4. Call Repository to Create Transaction ---
        System.out.println("Attempting to create outbound transaction:");
        System.out.println("  Product: " + selectedProduct.getClass());
        System.out.println("  Quantity: " + quantity);
        System.out.println("  Shipping: " + selectedShippingMethod);
        System.out.println("  Date: " + selectedDate);
        System.out.println("  Status: " + selectedStatus);
        System.out.println("  Address: " + destinationAddress);
        // Assuming current user and warehouse ID are available
        // Timestamp timestamp = Timestamp.valueOf(selectedDate.atStartOfDay());
        // boolean success = transactionRepository.createOutboundTransaction(
        //         currentUser.getId(),
        //         timestamp,
        //         destinationAddress,
        //         selectedShippingMethod,
        //         selectedProduct.getSku(), // Pass SKU string
        //         quantity,
        //         selectedStatus
        // );

        boolean success = true; // Placeholder for repository call result

        // --- 5. Show Feedback and Navigate/Clear ---
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Outbound transaction created successfully!");
            clearForm();
            // Optionally navigate back to the list
            // navigateToTransactionsList(event);
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
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
    }
}