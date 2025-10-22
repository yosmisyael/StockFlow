package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.*;
import com.oop.stockflow.repository.ProductRepository;
import com.oop.stockflow.repository.TransactionRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class TransactionIndexController implements Initializable {
    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;

    // Navigation buttons
    @FXML
    private Button transactionsListBtn;

    // Stats labels
    @FXML private Label pendingLabel;
    @FXML private Label committedLabel;
    @FXML private Label inboundLabel;
    @FXML private Label outboundLabel;

    // Table and columns
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> skuColumn;
    @FXML private TableColumn<Transaction, String> productNameColumn;
    @FXML private TableColumn<Transaction, String> brandColumn;
    @FXML private TableColumn<Transaction, Timestamp> dateColumn;
    @FXML private TableColumn<Transaction, TransactionStatus> statusColumn;
    @FXML private TableColumn<Transaction, Void> actionsColumn;

    // === Data & Repositories ===
    private Warehouse currentWarehouse;
    private ObservableList<Transaction> allTransactionsList = FXCollections.observableArrayList();;
    private AuthenticatedUser currentUser;
    private final TransactionRepository transactionRepository = TransactionRepository.getInstance();
    private final ProductRepository productRepository = ProductRepository.getInstance();

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;
        loadSessionData();
        setupTableColumns();
        loadTransactions();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Memuat data pengguna dari sesi.
     */
    private void loadSessionData() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.err.println("Error: No authenticated user found!");
        } else {
             nameLabel.setText(currentUser.getName());
             roleLabel.setText(currentUser.getUserType().getDbValue());
        }
    }

    /**
     * Memuat data transaksi untuk staf saat ini.
     */
    private void loadTransactions() {
        if (currentUser == null) {
            transactionsTable.setItems(FXCollections.emptyObservableList());
            updateStatisticsDisplay(0, 0, 0, 0); // Reset stats
            return;
        }
        List<Transaction> dbTransactions = transactionRepository.getAllTransactionsByStaffId(currentUser.getId());
        allTransactionsList.setAll(dbTransactions);
        transactionsTable.setItems(allTransactionsList);
        transactionsTable.refresh();
        calculateAndDisplayStatistics();
    }

    /**
     * Menghitung dan menampilkan statistik berdasarkan allTransactionsList.
     */
    private void calculateAndDisplayStatistics() {
        if (allTransactionsList == null || allTransactionsList.isEmpty()) {
            updateStatisticsDisplay(0, 0, 0, 0);
            return;
        }

        long pendingCount = allTransactionsList.stream()
                .filter(t -> t.getStatus() == TransactionStatus.PENDING)
                .count();
        long committedCount = allTransactionsList.stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMMITTED)
                .count();
        long inboundCount = allTransactionsList.stream()
                .filter(t -> t instanceof InboundTransaction)
                .count();
        long outboundCount = allTransactionsList.stream()
                .filter(t -> t instanceof OutboundTransaction)
                .count();

        updateStatisticsDisplay(pendingCount, committedCount, inboundCount, outboundCount);
    }

    /** Helper untuk update label statistik */
    private void updateStatisticsDisplay(long pending, long committed, long inbound, long outbound) {
        pendingLabel.setText(String.valueOf(pending));
        committedLabel.setText(String.valueOf(committed));
        inboundLabel.setText(String.valueOf(inbound));
        outboundLabel.setText(String.valueOf(outbound));
    }


    /**
     * Mengatur CellValueFactory dan CellFactory untuk kolom tabel.
     */
    private void setupTableColumns() {
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("productSku"));

        // --- Product Name Column ---
        productNameColumn.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            int sku = transaction.getSku();
            String productName = productRepository.getProductNameBySku(sku);
            return new SimpleStringProperty(productName != null ? productName : "N/A");
        });

        brandColumn.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            int sku = transaction.getSku();
            // Panggil method baru di ProductRepository
            String brand = productRepository.getProductBrandBySku(sku);
            return new SimpleStringProperty(brand != null ? brand : "N/A"); // Tampilkan N/A jika null
        });

        // --- Date Column ---
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date")); // Mengambil Timestamp
        dateColumn.setCellFactory(column -> new TableCell<Transaction, Timestamp>() {
            // Format tanggal sesuai keinginan Anda
            private final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm");
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        });

        // --- Status Column ---
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<Transaction, TransactionStatus>() {
            @Override
            protected void updateItem(TransactionStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle(""); // Hapus style
                } else {
                    Label statusLabel = new Label(status.getDbValue().substring(0, 1).toUpperCase() + status.getDbValue().substring(1)); // Capitalize
                    statusLabel.setStyle(getStatusStyle(status));
                    statusLabel.setAlignment(Pos.CENTER);
                    statusLabel.setMaxWidth(Double.MAX_VALUE);
                    setGraphic(statusLabel);
                    setText(null);
                }
            }
        });

        // --- Actions Column ---
        actionsColumn.setCellFactory(column -> new TableCell<Transaction, Void>() {
            private final Button detailBtn = new Button("Detail");
            private final Button updateBtn = new Button("Update");
            private final HBox actionBox = new HBox(8, detailBtn, updateBtn);

            {
                actionBox.setAlignment(Pos.CENTER);
                detailBtn.getStyleClass().add("action-button-detail");
                updateBtn.getStyleClass().add("action-button-edit");

                detailBtn.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    handleDetailAction(transaction);
                });

                updateBtn.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    handleUpdateAction(transaction);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    boolean canUpdate = transaction.getStatus() == TransactionStatus.PENDING;
                    updateBtn.setDisable(!canUpdate);
                    updateBtn.setText(canUpdate ? "Update" : "View Only");
                    setGraphic(actionBox);
                }
            }
        });
    }

    /**
     * Mengembalikan string style CSS untuk status badge.
     */
    private String getStatusStyle(TransactionStatus status) {
        String baseStyle = "-fx-padding: 4 12; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: 600;";
        switch (status) {
            case COMMITTED: return baseStyle + "-fx-background-color: #d1fae5; -fx-text-fill: #065f46;"; // Hijau
            case PENDING:   return baseStyle + "-fx-background-color: #fef3c7; -fx-text-fill: #92400e;"; // Kuning
            case VOIDED:    return baseStyle + "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b;"; // Merah
            default:        return baseStyle + "-fx-background-color: #f3f4f6; -fx-text-fill: #374151;"; // Abu-abu
        }
    }

    /**
     * Memuat ulang data transaksi dari database.
     */
    public void refreshTable() {
        loadTransactions();
    }

    // navigation
    @FXML
    private void handleTransactionsList() {
        System.out.println("Refreshing Transactions List...");
        refreshTable();
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
        System.out.println("Logging out...");
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    private void handleDetailAction(Transaction transaction) {
        System.out.println("Viewing details for Transaction ID: " + transaction.getSku());
        showTransactionDetailsDialog(transaction);
    }

    private void handleUpdateAction(Transaction transaction) {
        System.out.println("Updating Transaction ID: " + transaction.getSku());
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            showAlert(Alert.AlertType.WARNING, "Update Not Allowed", "Only 'Pending' transactions can have their status updated.");
            return;
        }
        showUpdateStatusDialog(transaction);
    }

    // ==================== Dialogs ====================
    private void showTransactionDetailsDialog(Transaction transaction) {
        System.out.println("Showing Transaction details for Transaction ID: " + transaction.getSku());
    }

    private void showUpdateStatusDialog(Transaction transaction) {
        ChoiceDialog<TransactionStatus> dialog = new ChoiceDialog<>(
                TransactionStatus.PENDING,
                TransactionStatus.COMMITTED,
                TransactionStatus.VOIDED
        );
        dialog.setTitle("Update Transaction Status");
        dialog.setHeaderText("Update status for Transaction #" + transaction.getSku());
        dialog.setContentText("Choose the new status:");

        Optional<TransactionStatus> result = dialog.showAndWait();

        result.ifPresent(newStatus -> {
            boolean success = transactionRepository.updateTransactionStatus(transaction.getSku(), newStatus);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction status updated to " + newStatus.getDbValue() + ".");
                refreshTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update transaction status.");
            }
        });
    }

    // Helper showAlert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
