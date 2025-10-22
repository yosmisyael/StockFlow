package com.oop.stockflow.controller; // Sesuaikan package

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.*; // Import model & enum
import com.oop.stockflow.repository.ProductRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class ProductEditController {

    // === Data Konteks ===
    private Warehouse currentWarehouse; // Keep for navigating back
    private AuthenticatedUser currentUser; // Keep for navigating back
    private Product productToEdit; // Produk yang sedang diedit

    // === Repositori ===
    private final ProductRepository productRepository = ProductRepository.getInstance();

    // === FXML Fields ===
    // Sidebar User Info (Keep if they exist in FXML)
    @FXML private Label userNameLabel;
    @FXML private Label userRole;

    // Form Fields - Product Type (Disabled)
    @FXML private RadioButton rbDryGood;
    @FXML private RadioButton rbFresh;

    // Form Fields - Basic Info
    @FXML private TextField txtSku; // Read-only
    @FXML private TextField txtName;
    @FXML private TextField txtBrand;
    @FXML private TextArea txtDescription;

    // Form Fields - Pricing & Measurements
    @FXML private TextField txtPurchasePrice;
    @FXML private TextField txtWeight;
    @FXML private TextField txtVolume;
    // Quantity field removed from FXML/Controller

    // Form Fields - Dry Good Specific (Container VBox)
    @FXML private VBox dryGoodFields;
    @FXML private TextField txtReorderPoint;
    @FXML private TextField txtReorderQuantity;
    @FXML private TextField txtUnitsPerCase;

    // Form Fields - Fresh Product Specific (Container VBox)
    @FXML private VBox freshFields;
    @FXML private TextField txtRequiredTemp;
    @FXML private TextField txtDaysToAlert;

    // Action Buttons
    @FXML private Button btnSave;
    @FXML private Button btnCancel;


    /**
     * Menerima data dari controller sebelumnya dan setup UI.
     */
    public void initData(Warehouse warehouse, AuthenticatedUser user, Product product) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;
        this.productToEdit = product;

        // Basic validation on received data
        if (this.currentUser == null || this.productToEdit == null || this.currentWarehouse == null) {
            System.err.println("Error: User, Product, and Warehouse data are required for editing.");
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Cannot load product data for editing.");
            // Try to navigate back gracefully if possible
            navigateToProductList();
            return;
        }

        // Setup UI based on the product
        populateForm();
        setupFieldVisibility();
    }

    /**
     * Mengisi form dengan data dari productToEdit.
     */
    private void populateForm() {
        txtSku.setText(String.valueOf(productToEdit.getSku())); // Display SKU
        txtName.setText(productToEdit.getName());
        txtBrand.setText(productToEdit.getBrand());
        txtDescription.setText(productToEdit.getDescription());
        txtPurchasePrice.setText(productToEdit.getPurchasePrice() != null ? productToEdit.getPurchasePrice().toPlainString() : "");
        txtWeight.setText(String.valueOf(productToEdit.getWeightPerUnitKg()));
        txtVolume.setText(String.valueOf(productToEdit.getVolumePerUnitM3()));

        // Populate specific fields
        if (productToEdit instanceof DryGoodProduct dryGood) {
            txtReorderPoint.setText(String.valueOf(dryGood.getReorderPoint()));
            txtReorderQuantity.setText(String.valueOf(dryGood.getReorderQuantity()));
            txtUnitsPerCase.setText(String.valueOf(dryGood.getUnitsPerCase()));
        } else if (productToEdit instanceof FreshProduct fresh) {
            txtRequiredTemp.setText(fresh.getRequiredTemp() != null ? fresh.getRequiredTemp().toPlainString() : "");
            txtDaysToAlert.setText(String.valueOf(fresh.getDaysToAlertBeforeExpiry()));
        }
    }

    /**
     * Mengatur visibilitas field spesifik (Dry/Fresh) dan menonaktifkan radio button.
     */
    private void setupFieldVisibility() {
        rbDryGood.setDisable(true);
        rbFresh.setDisable(true);

        boolean isDryGood = productToEdit.getProductType() == ProductType.DRY_GOOD;

        rbDryGood.setSelected(isDryGood);
        rbFresh.setSelected(!isDryGood);

        dryGoodFields.setVisible(isDryGood);
        dryGoodFields.setManaged(isDryGood);
        freshFields.setVisible(!isDryGood);
        freshFields.setManaged(!isDryGood);
    }

    /**
     * Handles the "Save Changes" button click.
     */
    @FXML
    private void handleUpdateProduct(ActionEvent event) {
        String name = txtName.getText().trim();
        String brand = txtBrand.getText().trim();
        String description = txtDescription.getText().trim();
        String priceStr = txtPurchasePrice.getText();
        String weightStr = txtWeight.getText();
        String volumeStr = txtVolume.getText();

        if (name.isEmpty() || priceStr.isEmpty() || weightStr.isEmpty() || volumeStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Name, Price, Weight, and Volume are required.");
            return;
        }

        BigDecimal purchasePrice;
        double weight, volume;
        try {
            purchasePrice = new BigDecimal(priceStr);
            weight = Double.parseDouble(weightStr);
            volume = Double.parseDouble(volumeStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter valid numbers for Price, Weight, and Volume.");
            return;
        }

        productToEdit.setName(name);
        productToEdit.setBrand(brand);
        productToEdit.setDescription(description);
        productToEdit.setPurchasePrice(purchasePrice);
        productToEdit.setWeightPerUnitKg(weight);
        productToEdit.setVolumePerUnitM3(volume);

        try {
            if (productToEdit instanceof DryGoodProduct dryGood) {
                String reorderPointStr = txtReorderPoint.getText();
                String reorderQtyStr = txtReorderQuantity.getText();
                String unitsPerCaseStr = txtUnitsPerCase.getText();
                dryGood.setReorderPoint(reorderPointStr.isEmpty() ? 0 : Integer.parseInt(reorderPointStr));
                dryGood.setReorderQuantity(reorderQtyStr.isEmpty() ? 0 : Integer.parseInt(reorderQtyStr));
                dryGood.setUnitsPerCase(unitsPerCaseStr.isEmpty() ? 0 : Integer.parseInt(unitsPerCaseStr));
            } else if (productToEdit instanceof FreshProduct fresh) {
                String requiredTempStr = txtRequiredTemp.getText();
                String daysAlertStr = txtDaysToAlert.getText();
                fresh.setRequiredTemp(requiredTempStr.isEmpty() ? null : new BigDecimal(requiredTempStr));
                fresh.setDaysToAlertBeforeExpiry(daysAlertStr.isEmpty() ? 0 : Integer.parseInt(daysAlertStr));
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter valid numbers for the product type specific fields.");
            return;
        }

        boolean success = productRepository.updateProduct(productToEdit);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully!");
            navigateToProductList();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update product.");
        }
    }

    /**
     * Handles the "Cancel" button click.
     */
    @FXML
    private void cancelEdit(ActionEvent event) {
        navigateToProductList(); // Navigate back to the product list
    }

    // Helper: Navigate Back to Product List
    private void navigateToProductList() {
        if (currentWarehouse == null || currentUser == null) {
            System.err.println("Error: Cannot navigate back to product list - context lost.");
            StageManager.getInstance().navigate(View.WAREHOUSE_INDEX, "Warehouse List"); // Example fallback
            return;
        }
        System.out.println("Navigating back to Product List...");
        StageManager.getInstance().navigateWithData(
                View.PRODUCT_INDEX,
                "Product Management for " + currentWarehouse.getName(),
                (ProductIndexController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    // === Helper: Show Alert ===
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // === Helper: Add Numeric Listeners (Keep these for better UX) ===
    private void addNumericValidationListeners() {
        txtPurchasePrice.textProperty().addListener(createDecimalInputListener(txtPurchasePrice));
        txtWeight.textProperty().addListener(createDecimalInputListener(txtWeight));
        txtVolume.textProperty().addListener(createDecimalInputListener(txtVolume));
        txtReorderPoint.textProperty().addListener(createIntegerInputListener(txtReorderPoint));
        txtReorderQuantity.textProperty().addListener(createIntegerInputListener(txtReorderQuantity));
        txtUnitsPerCase.textProperty().addListener(createIntegerInputListener(txtUnitsPerCase));
        txtRequiredTemp.textProperty().addListener(createDecimalInputListener(txtRequiredTemp));
        txtDaysToAlert.textProperty().addListener(createIntegerInputListener(txtDaysToAlert));
    }

    private javafx.beans.value.ChangeListener<String> createIntegerInputListener(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
    }
    private javafx.beans.value.ChangeListener<String> createDecimalInputListener(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                int caretPos = textField.getCaretPosition();
                textField.setText(oldValue);
                textField.positionCaret(caretPos > 0 ? caretPos - 1 : 0);
            }
        };
    }

    // Keep handleLogout if the button exists in FXML
    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    public void goToStaffMenu(ActionEvent actionEvent) {
        StageManager.getInstance().navigateWithData(
                View.STAFF_INDEX,
                "Warehouse " + currentWarehouse.getId() + " Staff Management",
                (StaffIndexController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    public void goToWarehouseDashboard(ActionEvent actionEvent) {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_DASHBOARD,
                "Warehouse " + currentWarehouse.getId(),
                (WarehouseDashboardController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    public void goToProductIndex(ActionEvent actionEvent) {
        StageManager.getInstance().navigateWithData(
                View.PRODUCT_INDEX,
                "Warehouse " + currentWarehouse.getId() + " Product Management",
                (ProductIndexController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }
}