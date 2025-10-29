package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.*;
import com.oop.stockflow.repository.ProductRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionShowController {
    // repositories
    private final ProductRepository productRepository = ProductRepository.getInstance();

    // data
    private Warehouse currentWarehouse;
    private AuthenticatedUser currentUser;
    private Transaction currentTransaction;
    private Product relatedProduct;

    // fields
    @FXML
    private Label transactionId;
    @FXML
    private Label transactionDate;
    @FXML
    private Label transactionType;
    @FXML
    private Label status;
    @FXML
    private Label shippingMethod;
    @FXML
    private Label productName;
    @FXML
    private Label productSku;
    @FXML
    private Label productBrand;
    @FXML
    private Label productType;
    @FXML
    private Label destinationAddress;
    @FXML
    private Label quantity;
    @FXML
    private Label purchasePrice;
    @FXML
    private Label totalValue;
    @FXML
    private Label totalWeight;
    @FXML
    private Label weightPerUnit;
    @FXML
    private Label totalVolume;
    @FXML
    private Label volumePerUnit;
    @FXML
    private Label statusHeader;

    @FXML
    private  Label nameLabel;
    @FXML
    private Label roleLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label dateLabel;
    @FXML
    private Label initialLabel;


    /**
     * Called after FXML loading to pass data and initialize the transaction detail view.
     * Validates the transaction data, fetches related product information, and populates the view.
     *
     * @param warehouse The current warehouse context.
     * @param user The authenticated user viewing the transaction.
     * @param transaction The Transaction object to display (must not be null).
     */
    public void initData(Warehouse warehouse, AuthenticatedUser user, Transaction transaction) {
        currentUser = user;
        currentWarehouse = warehouse;
        if (transaction == null) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Cannot display transaction details: Data missing.");
            goToTransactionIndex();
            return;
        }
        currentTransaction = transaction;
        fetchRelatedProduct();
        populateView();
    }

    /**
     * Fetches the Product details based on the SKU in the currentTransaction.
     */
    private void fetchRelatedProduct() {
        if (currentTransaction != null) {
            this.relatedProduct = productRepository.getProductBySku(currentTransaction.getSku());
        }
        if (this.relatedProduct == null) {
            System.err.println("Warning: Could not fetch product details for SKU: " + currentTransaction.getSku());
        }
    }

    /**
     * Fills the FXML Labels with data from currentTransaction and relatedProduct.
     */
    private void populateView() {
        if (currentTransaction == null) {
            System.out.println("transaction object is null");
        } else {
            dateLabel.setText(DateTimeUtils.getCurrentDate());
            initialLabel.setText(StringUtils.getInitial(currentUser.getName()));
            roleLabel.setText(currentUser.getUserType().getDbValue());
            nameLabel.setText(currentUser.getName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            DecimalFormat weightFormat = new DecimalFormat("#,##0.00 'kg'");
            DecimalFormat volumeFormat = new DecimalFormat("#,##0.000 'm³'");

            // Transaction Header
            transactionId.setText("Transaction ID #" + currentTransaction.getId());
            transactionDate.setText(dateFormat.format(currentTransaction.getDate()));
            statusHeader.setText(StringUtils.toTitleCase(currentTransaction.getStatus().getDbValue()));
            if (currentTransaction.getStatus() == TransactionStatus.COMMITTED) {
                statusHeader.getStyleClass().add("status-badge-committed");
            } else if (currentTransaction.getStatus() == TransactionStatus.PENDING) {
                statusHeader.getStyleClass().add("status-badge-pending");
            } else {
                statusHeader.getStyleClass().add("status-badge-voided");
            }

            // Transaction Info Card
            transactionType.setText(StringUtils.toTitleCase(currentTransaction.getType().getDbValue()));
            status.setText(StringUtils.toTitleCase(currentTransaction.getStatus().getDbValue()));
            shippingMethod.setText(StringUtils.toTitleCase(currentTransaction.getShippingType().getDbValue()));
            updateBadgeStyles();

            productSku.setText(String.valueOf(currentTransaction.getSku()));

            if (relatedProduct != null) {
                productName.setText(StringUtils.toTitleCase(relatedProduct.getName()));
                productBrand.setText(relatedProduct.getBrand() != null ? StringUtils.toTitleCase(relatedProduct.getBrand()) : "N/A");
                productType.setText(StringUtils.toTitleCase(relatedProduct.getProductType().getDbValue()));
            } else {
                productName.setText("Product Not Found");
                productBrand.setText("N/A");
                productType.setText("N/A");
            }

            // Destination Address Card
            if (currentTransaction instanceof OutboundTransaction outbound) {
                destinationAddress.setText(outbound.getDestinationAddress() != null && !outbound.getDestinationAddress().isBlank()
                        ? outbound.getDestinationAddress()
                        : "N/A (Outbound)");
            } else {
                destinationAddress.setText("N/A (Inbound Transaction)");
                destinationAddress.getParent().getParent().setVisible(false);
                destinationAddress.getParent().getParent().setManaged(false);
            }

            // Quantity & Pricing Card
            int qty = currentTransaction.getQuantity();
            quantity.setText(String.valueOf(qty));

            BigDecimal price = BigDecimal.ZERO;
            if (relatedProduct != null && relatedProduct.getPurchasePrice() != null) {
                price = relatedProduct.getPurchasePrice();
                purchasePrice.setText(currencyFormat.format(price));
            } else {
                purchasePrice.setText("N/A");
            }

            // Calculate Total Value
            BigDecimal totalVal = price.multiply(new BigDecimal(qty)).setScale(2, RoundingMode.HALF_UP);
            totalValue.setText(currencyFormat.format(totalVal));

            // --- Physical Specs Card ---
            if (relatedProduct != null) {
                double wpu = relatedProduct.getWeightPerUnitKg();
                double vpu = relatedProduct.getVolumePerUnitM3();
                weightPerUnit.setText("(" + weightFormat.format(wpu) + " / unit)");
                volumePerUnit.setText("(" + volumeFormat.format(vpu) + " / unit)");
                totalWeight.setText(weightFormat.format(wpu * qty));
                totalVolume.setText(volumeFormat.format(vpu * qty));
            } else {
                weightPerUnit.setText("N/A");
                volumePerUnit.setText("N/A");
                totalWeight.setText("N/A");
                totalVolume.setText("N/A");
            }
        }

    }

    /**
     * Applies CSS style classes to badges based on enum values.
     * Assumes style classes like .badge-inbound, .badge-outbound,
     * .badge-committed, .badge-pending, .badge-voided exist in CSS.
     */
    private void updateBadgeStyles() {
        // Clear existing type styles
        transactionType.getStyleClass().removeAll("badge-inbound", "badge-outbound");
        // Add correct type style
        if (currentTransaction.getType() == TransactionType.INBOUND) {
            transactionType.getStyleClass().add("badge-inbound");
        } else {
            transactionType.getStyleClass().add("badge-outbound");
        }

        // Clear existing status styles
        status.getStyleClass().removeAll("badge-committed", "badge-pending", "badge-voided");
        // Add correct status style
        switch (currentTransaction.getStatus()) {
            case COMMITTED:
                status.getStyleClass().add("badge-committed");
                break;
            case PENDING:
                status.getStyleClass().add("badge-pending");
                break;
            case VOIDED:
                status.getStyleClass().add("badge-voided");
                break;
        }
    }

    // navigations
    @FXML
    private void goToTransactionIndex() {
        System.out.println("Back button clicked.");
        StageManager.getInstance().navigateWithData(
                View.TRANSACTION_INDEX,
                "Transactions List",
                (TransactionIndexController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
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

    // action handlers
    @FXML
    private void handleLogout() {
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    // helper methods

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