package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.*;
import com.oop.stockflow.repository.ProductRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductCreateController implements Initializable {

    // === Context Data ===
    private Warehouse currentWarehouse;
    private AuthenticatedUser currentUser;

    // === Repositories ===
    private final ProductRepository productRepository = ProductRepository.getInstance();

    // Sidebar Buttons
    @FXML
    private Button btnTransactionsList;
    @FXML
    private Button btnInbound;
    @FXML
    private Button btnOutbound;
    @FXML
    private Button btnAddStock;
    @FXML
    private Button btnSettings;
    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label initialLabel;

    // Form Fields - Product Type
    @FXML
    private ToggleGroup productTypeGroup;
    @FXML
    private RadioButton rbDryGood;
    @FXML
    private RadioButton rbFresh;

    // Form Fields - Basic Info
    @FXML
    private TextField txtSku;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtBrand;
    @FXML
    private TextArea txtDescription;

    // Form Fields - Pricing & Measurements
    @FXML
    private TextField txtPurchasePrice;
    @FXML
    private TextField txtWeight;
    @FXML
    private TextField txtVolume;
    @FXML
    private TextField txtQuantity;

    // Form Fields - Dry Good Specific (Container VBox)
    @FXML
    private VBox dryGoodFields;
    @FXML
    private TextField txtReorderPoint;
    @FXML
    private TextField txtReorderQuantity;
    @FXML
    private TextField txtUnitsPerCase;

    // Form Fields - Fresh Product Specific (Container VBox)
    @FXML
    private VBox freshFields;
    @FXML
    private TextField txtRequiredTemp;
    @FXML
    private TextField txtDaysToAlert;

    // Action Buttons
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;

    /**
     * Called by JavaFX after FXML loading but before initData.
     * Use this for setting up listeners.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupProductTypeToggleListener();
        addNumericValidationListeners();
        dryGoodFields.setVisible(true);
        dryGoodFields.setManaged(true);
        freshFields.setVisible(false);
        freshFields.setManaged(false);
    }

    /**
     * Public method to receive context data after initialization.
     */
    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;
        loadPageContext();
        if (this.currentUser == null) {
            System.err.println("Error: AuthenticatedUser is required for Add Stock.");
            handleLogout(null);
        }
    }

    /**
     * Adds listener to product type radio buttons to show/hide specific fields.
     */
    private void setupProductTypeToggleListener() {
        productTypeGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == rbDryGood) {
                dryGoodFields.setVisible(true);
                dryGoodFields.setManaged(true);
                freshFields.setVisible(false);
                freshFields.setManaged(false);
            } else if (newToggle == rbFresh) {
                dryGoodFields.setVisible(false);
                dryGoodFields.setManaged(false);
                freshFields.setVisible(true);
                freshFields.setManaged(true);
            }
        });
    }

    /**
     * Adds listeners to numeric fields to restrict input.
     */
    private void addNumericValidationListeners() {
        // Allow only numbers (integer)
        txtQuantity.textProperty().addListener(createIntegerInputListener(txtQuantity));
        txtReorderPoint.textProperty().addListener(createIntegerInputListener(txtReorderPoint));
        txtReorderQuantity.textProperty().addListener(createIntegerInputListener(txtReorderQuantity));
        txtUnitsPerCase.textProperty().addListener(createIntegerInputListener(txtUnitsPerCase));
        txtDaysToAlert.textProperty().addListener(createIntegerInputListener(txtDaysToAlert));

        // Allow numbers and decimal point (double/BigDecimal)
        txtPurchasePrice.textProperty().addListener(createDecimalInputListener(txtPurchasePrice));
        txtWeight.textProperty().addListener(createDecimalInputListener(txtWeight));
        txtVolume.textProperty().addListener(createDecimalInputListener(txtVolume));
        txtRequiredTemp.textProperty().addListener(createDecimalInputListener(txtRequiredTemp)); // Allow negative temps too? Add '-' if needed
    }

    // Helper for integer validation listener
    private ChangeListener<String> createIntegerInputListener(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
    }

    // Helper for decimal validation listener
    private ChangeListener<String> createDecimalInputListener(TextField textField) {
        return (observable, oldValue, newValue) -> {
            // Allows numbers, one decimal point. Add '-' at start if needed for temp: "^-?..."
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                // Try to preserve cursor position somewhat
                int caretPos = textField.getCaretPosition();
                textField.setText(oldValue);
                textField.positionCaret(caretPos > 0 ? caretPos - 1 : 0);
            }
        };
    }

    // === Action Handlers ===

    /**
     * Handles the "+ Add Product" button click.
     * Gathers data, validates, creates the correct Product subclass, and calls repository.
     */
    @FXML
    private void createProduct() {
        String name = txtName.getText().trim();
        String brand = txtBrand.getText().trim();
        String description = txtDescription.getText().trim();
        String priceStr = txtPurchasePrice.getText();
        String weightStr = txtWeight.getText();
        String volumeStr = txtVolume.getText();
        String quantityStr = txtQuantity.getText();

        if (name.isEmpty() || priceStr.isEmpty() || weightStr.isEmpty() || volumeStr.isEmpty() || quantityStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "SKU, Name, Price, Weight, Volume, and Initial Quantity are required.");
            return;
        }

        BigDecimal purchasePrice;
        double weight, volume;
        int quantity;
        try {
            purchasePrice = new BigDecimal(priceStr);
            weight = Double.parseDouble(weightStr);
            volume = Double.parseDouble(volumeStr);
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) throw new NumberFormatException("Quantity cannot be negative");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter valid numbers for Price, Weight, Volume, and Quantity.");
            return;
        }

        Product productToAdd = null;
        ProductType selectedType = rbDryGood.isSelected() ? ProductType.DRY_GOOD : ProductType.FRESH;

        try {
            if (selectedType == ProductType.DRY_GOOD) {
                // Get Dry Good Fields
                String reorderPointStr = txtReorderPoint.getText();
                String reorderQtyStr = txtReorderQuantity.getText();
                String unitsPerCaseStr = txtUnitsPerCase.getText();

                // Parse Dry Good Fields (allow empty -> default 0)
                int reorderPoint = reorderPointStr.isEmpty() ? 0 : Integer.parseInt(reorderPointStr);
                int reorderQuantity = reorderQtyStr.isEmpty() ? 0 : Integer.parseInt(reorderQtyStr);
                int unitsPerCase = unitsPerCaseStr.isEmpty() ? 0 : Integer.parseInt(unitsPerCaseStr);

                productToAdd = new DryGoodProduct(name, brand, description, purchasePrice, weight, volume,
                        quantity, reorderPoint, reorderQuantity, unitsPerCase, currentWarehouse.getId());

            } else {
                String requiredTempStr = txtRequiredTemp.getText();
                String daysAlertStr = txtDaysToAlert.getText();

                BigDecimal requiredTemp = requiredTempStr.isEmpty() ? null : new BigDecimal(requiredTempStr);
                int daysToAlert = daysAlertStr.isEmpty() ? 0 : Integer.parseInt(daysAlertStr);

                productToAdd = new FreshProduct(name, brand, description, purchasePrice, weight, volume,
                        quantity, requiredTemp, daysToAlert, currentWarehouse.getId());
                System.out.println("checikg " + currentWarehouse.getId());
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter valid numbers for the product type specific fields.");
            return;
        }

        boolean success = productRepository.addProduct(productToAdd);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Product '" + name + "' added successfully!");
            clearForm();
            navigateToProductList();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add product. SKU might already exist.");
        }

    }

    /**
     * Handles the "Cancel" button click.
     */
    @FXML
    private void cancelCreate(ActionEvent event) {
        System.out.println("Cancel button clicked.");
        navigateToProductList();
    }

    @FXML
    private void goToWarehouseDashboard() {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_DASHBOARD,
                "Warehouse " + currentWarehouse.getId(),
                (WarehouseDashboardController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
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
        StageManager.getInstance().navigateWithData(
                View.PRODUCT_INDEX,
                "Warehouse " + currentWarehouse.getId() + " Product Management",
                (ProductIndexController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logging out...");
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    // === Helper: Navigate Back (Example) ===
    private void navigateToProductList() {
        StageManager.getInstance().navigateWithData(
                View.PRODUCT_INDEX,
                "Warehouse " + currentWarehouse.getId() + " Product Management",
                (ProductIndexController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    // === Helper: Clear Form ===
    private void clearForm() {
        txtName.clear();
        txtBrand.clear();
        txtDescription.clear();
        txtPurchasePrice.clear();
        txtWeight.clear();
        txtVolume.clear();
        txtQuantity.clear();
        txtReorderPoint.clear();
        txtReorderQuantity.clear();
        txtUnitsPerCase.clear();
        txtRequiredTemp.clear();
        txtDaysToAlert.clear();
        rbDryGood.setSelected(true);
    }

    // === Helper: Show Alert ===
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // helper methods
    private void loadPageContext() {
        nameLabel.setText(currentUser.getName());
        roleLabel.setText(currentUser.getUserType().getDbValue());
        dateLabel.setText(DateTimeUtils.getCurrentDate());
        initialLabel.setText(StringUtils.getInitial(currentUser.getName()));
    }
}