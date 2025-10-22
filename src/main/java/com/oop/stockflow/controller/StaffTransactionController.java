package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Product;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.ProductRepository;
import com.oop.stockflow.repository.TransactionRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StaffTransactionController implements Initializable {
    // sidebar fields
    @FXML private Label staffNameLabel;

    // statistics fields
    @FXML private Label todayInboundLabel;
    @FXML private Label totalValueLabel;
    @FXML private Label pendingLabel;
    @FXML private Label suppliersLabel;

    // form fields
    @FXML private ToggleGroup transactionTypeGroup;
    @FXML private RadioButton inboundRadio;
    @FXML private RadioButton outboundRadio;
    @FXML private Label supplierLabel;
    @FXML private ComboBox<String> supplierCombo;
    @FXML private ComboBox<Product> productSKUCombo;
    @FXML private TextField quantityField;
    @FXML private DatePicker transactionDatePicker;
    @FXML private ComboBox<String> shippingMethodCombo;
    @FXML private VBox addressContainer;
    @FXML private TextArea destinationAddressArea;

    // initialized repositories and data
    private AuthenticatedUser currentUser;
    private Warehouse currentWarehouse;
    private final TransactionRepository transactionRepository = TransactionRepository.getInstance();
    private final ProductRepository productRepository = ProductRepository.getInstance();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSessionData();

        loadStats();

        populateComboBoxes();

        transactionTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                handleTransactionTypeChange();
            }
        });

        inboundRadio.setSelected(true);
        handleTransactionTypeChange();
        transactionDatePicker.setValue(LocalDate.now());
    }

    /**
     * Memuat data pengguna dari sesi dan menentukan ID gudang.
     */
    private void loadSessionData() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            staffNameLabel.setText(currentUser.getName());
            // Asumsi AuthenticatedUser memiliki warehouseId, ini sangat penting!
            // Jika tidak, Anda perlu mengambilnya dari StaffRepository.
            // currentWarehouseId = staffRepository.getStaffById(currentUser.getId()).getWarehouseId();
//            currentWarehouseId = currentUser.getWarehouseId(); // Asumsi getter ini ada
        } else {
            // Handle jika tidak ada sesi (seharusnya tidak terjadi)
            staffNameLabel.setText("Guest");
        }
    }

    /**
     * Mengambil dan menampilkan data statistik untuk gudang saat ini.
     * (Anda perlu membuat method-method ini di repositori Anda)
     */
    private void loadStats() {
        // todayInboundLabel.setText(String.valueOf(transactionRepository.getInboundCountToday(currentWarehouseId)));
        // totalValueLabel.setText("Rp " + transactionRepository.getTotalValueFormatted(currentWarehouseId));
        // pendingLabel.setText(String.valueOf(transactionRepository.getPendingCount(currentWarehouseId)));
        // suppliersLabel.setText(String.valueOf(supplierRepository.getActiveCount()));
    }

    /**
     * Mengisi ComboBox dengan data dari database.
     */
    private void populateComboBoxes() {
        // Isi produk
//        productSKUCombo.setItems(FXCollections.observableArrayList(productRepository.getAllProducts()));
        // Isi supplier
//        supplierCombo.setItems(FXCollections.observableArrayList(supplierRepository.getAllSuppliers()));
        // Isi metode pengiriman
        // Asumsi Anda punya enum ShippingMethod
        // shippingMethodCombo.setItems(FXCollections.observableArrayList(ShippingMethod.values()));
    }


    /**
     * Handler untuk mengubah UI saat radio button Inbound/Outbound dipilih.
     */
    @FXML
    private void handleTransactionTypeChange() {
        boolean isOutbound = outboundRadio.isSelected();
        addressContainer.setVisible(isOutbound);
        addressContainer.setManaged(isOutbound);
        supplierLabel.setText(isOutbound ? "Customer" : "Supplier");
    }

    /**
     * Handler untuk tombol "Submit" atau "Create Transaction".
     */
    @FXML
    private void handleSubmit() {
        // Validasi input
        if (productSKUCombo.getValue() == null || supplierCombo.getValue() == null || quantityField.getText().isEmpty() || transactionDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill all required fields.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Quantity must be a positive number.");
            return;
        }

        // Ambil nilai dari form
        String transactionType = inboundRadio.isSelected() ? "inbound" : "outbound";
        Product selectedProduct = productSKUCombo.getValue();
        LocalDate date = transactionDatePicker.getValue();
        String shippingMethod = shippingMethodCombo.getValue();
        String address = outboundRadio.isSelected() ? destinationAddressArea.getText() : null;

//        boolean success = transactionRepository.createTransaction(
//                currentUser.getId(),
//                selectedSupplier.getId(),
//                date,
//                transactionType,
//                selectedProduct.getSku(),
//                quantity,
//                currentWarehouseId,
//                address,
//                shippingMethod
//        );
        boolean success = true;

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction has been recorded successfully.");
            clearForm();
            loadStats(); // Refresh kartu statistik
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to record transaction. Check stock levels or warehouse capacity.");
        }
    }

    /**
     * Mengosongkan form setelah submit berhasil.
     */
    private void clearForm() {
        productSKUCombo.getSelectionModel().clearSelection();
        supplierCombo.getSelectionModel().clearSelection();
        quantityField.clear();
        destinationAddressArea.clear();
        shippingMethodCombo.getSelectionModel().clearSelection();
        transactionDatePicker.setValue(LocalDate.now());
    }

    /**
     * Method helper untuk menampilkan dialog Alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // sidebar navigations
    @FXML
    private void navigateToInbound() {
        System.out.println("Already on Inbound page.");
        inboundRadio.setSelected(true);
    }

    @FXML
    private void navigateToOutbound() {
        System.out.println("Switching to Outbound view.");
        outboundRadio.setSelected(true);
    }

    @FXML
    private void navigateToInventory() {
        // StageManager.getInstance().navigateTo(View.STAFF_INVENTORY, "Inventory");
    }

    @FXML
    private void navigateToReports() {
        // StageManager.getInstance().navigateTo(View.STAFF_REPORTS, "Reports");
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }
}