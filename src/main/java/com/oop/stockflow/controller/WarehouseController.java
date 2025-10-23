package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.ProductRepository;
import com.oop.stockflow.repository.StaffRepository;
import com.oop.stockflow.repository.WarehouseRepository;
import com.oop.stockflow.utils.StringUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.List;

public class WarehouseController {
    @FXML
    private GridPane warehouseContainer;
    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label initialLabel;

    final private WarehouseRepository warehouseRepository;
    final private ProductRepository productRepository;
    final private StaffRepository staffRepository;

    private AuthenticatedUser currentUser;

    public void initData(AuthenticatedUser user) {
        currentUser = user;
        loadPageContext();
        loadWarehouses();
    }

    public WarehouseController() {
        this.warehouseRepository = WarehouseRepository.getInstance();
        this.productRepository = ProductRepository.getInstance();
        this.staffRepository = StaffRepository.getInstance();
    }

    // navigations
    @FXML
    private void goToAddWarehouse(ActionEvent event) throws IOException {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_CREATE,
                "Add Warehouse",
                (WarehouseCreateController controller) -> {
                    controller.initData(currentUser);
                }
        );
    }

    // action handlers
    @FXML
    private void handleLogout() {
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    // helper methods
    private void loadWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.getAllWarehouses();

        int col = 0;
        int row = 0;
        int maxCols = 4;

        for (Warehouse w : warehouses) {
            VBox card = createWarehouseCard(w);

            // Set the grid position for each card
            GridPane.setColumnIndex(card, col);
            GridPane.setRowIndex(card, row);

            warehouseContainer.add(card, col, row);

            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createWarehouseCard(Warehouse warehouse) {
        // Outer card
        VBox card = new VBox(20);
        card.getStyleClass().add("warehouse-card");
        card.setPadding(new Insets(24));

        // Top sections
        // Left initials label
        Label initials = new Label(StringUtils.getInitial(warehouse.getName()));
        initials.getStyleClass().add("warehouse-icon-blue");
        initials.setFont(Font.font("System Bold", 30));
        initials.setAlignment(Pos.CENTER);
        initials.setPadding(new Insets(0, 8, 0, 8));
        initials.setStyle("-fx-background-radius: 10;");

        // Warehouse name and address
        Label nameLabel = new Label(warehouse.getName());
        nameLabel.getStyleClass().add("warehouse-name");
        nameLabel.setFont(Font.font("System Bold", 20));

        Label addressLabel = new Label(warehouse.getAddress());
        addressLabel.getStyleClass().add("warehouse-address");
        addressLabel.setFont(Font.font(16));

        VBox infoBox = new VBox(2, nameLabel, addressLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Combine initials and info
        HBox nameRow = new HBox(10, initials, infoBox);
        VBox.setVgrow(nameRow, Priority.NEVER);

        // Status Label (Active)
        Label statusLabel = new Label("Active");
        statusLabel.getStyleClass().add("status-badge-active");
        statusLabel.setPadding(new Insets(4, 12, 4, 12));
        statusLabel.setFont(Font.font(12));
        statusLabel.setStyle("-fx-background-radius: 32; -fx-font-size: 12;");
        VBox.setMargin(statusLabel, new Insets(0, 0, 0, 60));

        VBox topBox = new VBox(4, nameRow, statusLabel);
        HBox headerBox = new HBox(topBox);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setFillHeight(false);

        // Stats Section
        // calculate staff
        int totalStaff = staffRepository.countStaffByWarehouseId(warehouse.getId());
        VBox staffBox = new VBox(4,
                styledLabel(String.valueOf(totalStaff), 28, true, "warehouse-stat-value"),
                styledLabel("Staff", 12, false, "warehouse-stat-label")
        );
        staffBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(staffBox, Priority.ALWAYS);

        // calculate stock
        int stockTotal = productRepository.countProductsByWarehouseId(warehouse.getId());
        VBox stockBox = new VBox(4,
                styledLabel(String.valueOf(stockTotal), 28, true, "warehouse-stat-value"),
                styledLabel("Stock Items", 12, false, "warehouse-stat-label")
        );
        stockBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(stockBox, Priority.ALWAYS);

        HBox statsBox = new HBox(40, staffBox, stockBox);
        statsBox.setAlignment(Pos.CENTER);

        // Button
        Button viewBtn = new Button("View Details");
        viewBtn.getStyleClass().add("view-details-button");
        viewBtn.setFont(Font.font("System Bold", 16));
        viewBtn.setMaxWidth(Double.MAX_VALUE);

        viewBtn.setOnAction(event -> {
            StageManager.getInstance().navigateWithData(
                    View.WAREHOUSE_DASHBOARD,
                    "Warehouse Dashboard of " + warehouse.getName(),
                    (WarehouseDashboardController controller) -> controller.initData(warehouse, currentUser)
            );
        });

        // Combine all
        card.getChildren().addAll(headerBox, statsBox, viewBtn);

        return card;
    }

    private Label styledLabel(String text, double size, boolean bold, String cssClass) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add(cssClass);
        lbl.setFont(Font.font(bold ? "System Bold" : "System", size));
        return lbl;
    }

    private void loadPageContext() {
        nameLabel.setText(currentUser.getName());
        roleLabel.setText(currentUser.getUserType().getDbValue());
        initialLabel.setText(StringUtils.getInitial(currentUser.getName()));
    }
}
