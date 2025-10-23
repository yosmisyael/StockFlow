package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Product;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.ProductRepository;
import com.oop.stockflow.repository.TransactionRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
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

    // Stats Cards
    @FXML
    private Label totalStockLabel;
    @FXML
    private Label outboundTodayLabel;
    @FXML
    private Label inboundTodayLabel;
    @FXML
    private Label lowStockLabel;

    // Products Table
    @FXML
    private TableView<Product> productsTable;
    @FXML
    private TableColumn<Product, Integer> skuColumn;
    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, String> brandColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, Void> actionsColumn;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        productsTable.setItems(productList);
    }

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;

        if (this.currentUser == null) {
            System.err.println("Error: AuthenticatedUser is required for Product Index.");
            return;
        }

        loadPageContext();
        loadStats();
        loadProductData();
    }

    private void loadStats() {
        // count inbound today
        int countInboundToday = 0;
        countInboundToday = transactionRepository.countTodayInboundTransaction();
        inboundTodayLabel.setText(String.valueOf(countInboundToday));

        // count outbound today
        int countOutboundToday = 0;
        countOutboundToday = transactionRepository.countTodayOutboundTransaction();
        outboundTodayLabel.setText(String.valueOf(countOutboundToday));

        // count low stock
        int countLowStock = 0;
        countLowStock = productRepository.countLowStockByWarehouseId(currentWarehouse.getId());
        lowStockLabel.setText(String.valueOf(countLowStock));

        // calculate stock
        int countStock = 0;
        countStock = productRepository.countProductsByWarehouseId(currentWarehouse.getId());
        totalStockLabel.setText(String.valueOf(countStock));
    }

    private void setupTableColumns() {
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        actionsColumn.setCellFactory(column -> new TableCell<Product, Void>() {
            private final Button showBtn = new Button("Show");
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(8, showBtn, editBtn, deleteBtn);

            {
                pane.setAlignment(Pos.CENTER_LEFT);
                showBtn.getStyleClass().add("action-button-show");
                editBtn.getStyleClass().add("action-button-edit");
                deleteBtn.getStyleClass().add("action-button-delete");

                showBtn.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    handleShowProduct(product);
                });

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

    private void loadProductData() {
        List<Product> productsFromDb = productRepository.getAllProductsByWarehouseId(currentWarehouse.getId());
        productList.setAll(productsFromDb);
        productsTable.refresh();
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

    private void handleShowProduct(Product product) {
        StageManager.getInstance().navigateWithData(
                View.PRODUCT_SHOW,
                "Product Detail of " + product.getName(),
                (ProductShowController controller) -> controller.initData(currentWarehouse, currentUser, product)
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
    private void handleLogout() {
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

    // helper methods
    private void loadPageContext() {
        nameLabel.setText(currentUser.getName());
        roleLabel.setText(currentUser.getUserType().getDbValue());
        dateLabel.setText(DateTimeUtils.getCurrentDate());
        initialLabel.setText(StringUtils.getInitial(currentUser.getName()));
    }
}