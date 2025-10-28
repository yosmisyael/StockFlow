package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.*;
import com.oop.stockflow.repository.ProductRepository;
import com.oop.stockflow.repository.TransactionRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

public class TransactionIndexController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label initialLabel;
    @FXML
    private Label dateLabel;

    // Navigation buttons
    @FXML
    private Button transactionsListBtn;

    // Stats labels
    @FXML
    private Label pendingLabel;
    @FXML
    private Label committedLabel;
    @FXML
    private Label inboundLabel;
    @FXML
    private Label outboundLabel;

    // Table and columns
    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TableColumn<Transaction, Integer> idColumn;
    @FXML
    private TableColumn<Transaction, String> productNameColumn;
    @FXML
    private TableColumn<Transaction, String> brandColumn;
    @FXML
    private TableColumn<Transaction, Timestamp> dateColumn;
    @FXML
    private TableColumn<Transaction, TransactionStatus> statusColumn;
    @FXML
    private TableColumn<Transaction, TransactionType> typeColumn;
    @FXML
    private TableColumn<Transaction, Void> actionsColumn;

    // data and repositories
    private Warehouse currentWarehouse;
    private ObservableList<Transaction> allTransactionsList = FXCollections.observableArrayList();
    ;
    private AuthenticatedUser currentUser;
    private final TransactionRepository transactionRepository = TransactionRepository.getInstance();
    private final ProductRepository productRepository = ProductRepository.getInstance();

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;
        loadSessionData();
        setupTableColumns();
        loadTransactions();
        loadPageContext();
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
            updateStatisticsDisplay(0, 0, 0, 0);
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

    /**
     * Helper untuk update label statistik
     */
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
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        productNameColumn.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            int sku = transaction.getSku();
            String productName = productRepository.getProductNameBySku(sku);
            return new SimpleStringProperty(productName != null ? productName : "N/A");
        });

        brandColumn.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            int sku = transaction.getSku();
            String brand = productRepository.getProductBrandBySku(sku);
            return new SimpleStringProperty(brand != null ? brand : "N/A");
        });

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(column -> new TableCell<Transaction, Timestamp>() {
            private final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm");

            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<Transaction, TransactionStatus>() {
            @Override
            protected void updateItem(TransactionStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("");
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

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setCellFactory(column -> new TableCell<Transaction, TransactionType>() {
            @Override
            protected void updateItem(TransactionType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item.getDbValue().substring(0, 1).toUpperCase() + item.getDbValue().substring(1));
                    FontIcon icon = new FontIcon(item == TransactionType.INBOUND ? "fas-arrow-down" : "fas-arrow-up");
                    icon.setIconSize(14);
                    setGraphic(icon);
                    setStyle(item == TransactionType.INBOUND ? "-fx-text-fill: green;" : "-fx-text-fill: blue;");
                }
            }
        });

        actionsColumn.setCellFactory(column -> new TableCell<Transaction, Void>() {
            private final Button detailBtn = new Button("Detail");
            private final Button updateBtn = new Button("Update");
            private final HBox actionBox = new HBox(8, detailBtn, updateBtn);

            {
                actionBox.setAlignment(Pos.CENTER_LEFT);
                detailBtn.getStyleClass().add("action-button-show");
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
            case COMMITTED:
                return baseStyle + "-fx-background-color: #d1fae5; -fx-text-fill: #065f46;"; // Hijau
            case PENDING:
                return baseStyle + "-fx-background-color: #fef3c7; -fx-text-fill: #92400e;"; // Kuning
            case VOIDED:
                return baseStyle + "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b;"; // Merah
            default:
                return baseStyle + "-fx-background-color: #f3f4f6; -fx-text-fill: #374151;"; // Abu-abu
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
        StageManager.getInstance().navigateWithData(
                View.TRANSACTION_SHOW,
                "Transaction Detail for ID" + transaction.getId(),
                (TransactionShowController controller) -> { controller.initData(currentWarehouse, currentUser, transaction); }
        );
    }

    private void handleUpdateAction(Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            showAlert(Alert.AlertType.WARNING, "Update Not Allowed", "Only 'Pending' transactions can have their status updated.");
            return;
        }
        if (currentWarehouse.getStatus() != WarehouseStatus.ACTIVE) {
            showAlert(Alert.AlertType.WARNING, "Prohibited Action", "You are not allowed to perform any transaction on non active warehouse.");
            return;
        }
        showUpdateStatusDialog(transaction);
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
            boolean success = transactionRepository.updateTransactionStatus(transaction.getId(), newStatus);
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

    private void loadPageContext() {
        dateLabel.setText(DateTimeUtils.getCurrentDate());
        initialLabel.setText(StringUtils.getInitial(currentUser.getName()));
    }
}
