package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Product;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.ProductRepository;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductIndexController implements Initializable {
    private Warehouse currentWarehouse;
    private AuthenticatedUser currentUser;

    private final ProductRepository productRepository = ProductRepository.getInstance(); // Asumsi Singleton

    // === FXML Fields ===

     @FXML private Label userNameLabel; // Nama di navbar atas

     @FXML private Label sidebarUserNameLabel;
     @FXML private Label sidebarUserRoleLabel;

     @FXML private Label totalStockLabel;
     @FXML private Label expirySoonLabel;
     @FXML private Label lowStockLabel;
     @FXML private Label pendingTransactionsLabel;

    // Products Table
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Integer> skuColumn;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, String> brandColumn;
    @FXML private TableColumn<Product, Integer> quantityColumn;
    @FXML private TableColumn<Product, Void> actionsColumn;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("ProductIndexController initializing (FXML injected)...");
        setupTableColumns();
        productsTable.setItems(productList);
    }

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;

        if (this.currentUser == null) {
            System.err.println("Error: AuthenticatedUser is required for Product Index.");
            handleLogout(null);
            return;
        }

        loadUserData();
        loadStatistics();
        loadProductData();
    }

    /**
     * Mengisi data pengguna di UI (misalnya, sidebar).
     */
    private void loadUserData() {
        // if (currentUser != null && sidebarUserNameLabel != null && sidebarUserRoleLabel != null) {
        //    sidebarUserNameLabel.setText(currentUser.getName());
        //    sidebarUserRoleLabel.setText(currentUser.getUserType().toString()); // Sesuaikan
        // }
    }

    /**
     * Placeholder untuk memuat data statistik ke kartu di atas.
     */
    private void loadStatistics() {
        System.out.println("Placeholder: Loading statistics...");
        // TODO: Panggil repositori untuk mendapatkan data statistik
        // Contoh:
        // int totalItems = productRepository.getTotalStockCount(currentWarehouse.getId());
        // int expiring = productRepository.getExpiringSoonCount(currentWarehouse.getId(), 30);
        // ...
        // totalStockLabel.setText(String.valueOf(totalItems));
        // expirySoonLabel.setText(String.valueOf(expiring));
    }

    /**
     * Mengatur CellValueFactory dan CellFactory untuk kolom tabel produk.
     */
    private void setupTableColumns() {
        // Sesuaikan "property" dengan nama getter di kelas Product Anda
        // (misal, getSku() -> "sku", getName() -> "name")
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Setup kolom Aksi (dengan tombol Edit/Delete)
        actionsColumn.setCellFactory(column -> new TableCell<Product, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(8, editBtn, deleteBtn); // Spasi 8

            {
                pane.setAlignment(Pos.CENTER);
                // Terapkan style class dari CSS Anda
                editBtn.getStyleClass().add("action-button-edit");
                deleteBtn.getStyleClass().add("action-button-delete");

                editBtn.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    handleEditProduct(product);
                });

                deleteBtn.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    handleDeleteProduct(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    /**
     * Memuat data produk dari repositori dan menampilkannya di tabel.
     */
    private void loadProductData() {
        System.out.println("Loading product data...");
        // TODO: Implementasikan getAllProducts() atau getProductsByWarehouse(id)
        //       di ProductRepository Anda.
        List<Product> productsFromDb = productRepository.getAllProducts();
        productList.setAll(productsFromDb);
        productsTable.refresh();
        System.out.println("Loaded " + productList.size() + " products.");
    }

    // action handlers

    @FXML
    private void handleAddNewProduct(ActionEvent event) {
        System.out.println("Add New Product button clicked.");
        // TODO: Navigasi ke halaman Add Product
        // StageManager.getInstance().navigateTo(View.PRODUCT_ADD, "Add New Product");
        showAlert(Alert.AlertType.INFORMATION, "Action", "Add New Product navigation not implemented yet.");
    }

    @FXML
    private void handlePrevious(ActionEvent event) {
        System.out.println("Previous Page button clicked.");
        // TODO: Implementasi logika pagination - halaman sebelumnya
        showAlert(Alert.AlertType.INFORMATION, "Action", "Pagination (Previous) not implemented yet.");
    }

    @FXML
    private void handlePage2(ActionEvent event) { // Contoh untuk tombol halaman 2
        System.out.println("Page 2 button clicked.");
        // TODO: Implementasi logika pagination - pergi ke halaman 2
        showAlert(Alert.AlertType.INFORMATION, "Action", "Pagination (Page 2) not implemented yet.");
    }
    @FXML
    private void handlePage3(ActionEvent event) { // Contoh untuk tombol halaman 3
        System.out.println("Page 3 button clicked.");
        // TODO: Implementasi logika pagination - pergi ke halaman 3
        showAlert(Alert.AlertType.INFORMATION, "Action", "Pagination (Page 3) not implemented yet.");
    }

    @FXML
    private void handleNext(ActionEvent event) {
        System.out.println("Next Page button clicked.");
        // TODO: Implementasi logika pagination - halaman berikutnya
        showAlert(Alert.AlertType.INFORMATION, "Action", "Pagination (Next) not implemented yet.");
    }

    private void handleEditProduct(Product product) {
         StageManager.getInstance().navigateWithData(
              View.PRODUCT_EDIT,
              "Edit Product: " + product.getName(),
              (ProductEditController controller) -> controller.initData(currentWarehouse, currentUser, product)
         );
    }

    private void handleDeleteProduct(Product product) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Product?");
        confirmation.setContentText("Are you sure you want to delete '" + product.getName() + "' (SKU: " + product.getSku() + ")? This action might affect transaction history.");

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = productRepository.deleteProduct(product.getSku());

            if (deleted) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product '" + product.getName() + "' deleted successfully.");
                loadProductData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete product.");
            }
        }
    }


    // navigations
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
    private void goToProductCreate() {
        StageManager.getInstance().navigateWithData(
                View.PRODUCT_CREATE,
                "Warehouse " + currentWarehouse.getId() + "Add Product",
                (ProductCreateController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    @FXML
    private void goToDashboard() {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_DASHBOARD,
                "Warehouse " + currentWarehouse + " Dashboard",
                (WarehouseDashboardController controller) -> { controller.initData(currentWarehouse, currentUser); }
        );
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logging out...");
        SessionManager.getInstance().endSession();
    }

    // helper methods
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}