package com.oop.stockflow.controller;

import com.oop.stockflow.app.SceneManager;
import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.WarehouseRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WarehouseController implements Initializable {
    @FXML
    private GridPane warehouseContainer;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRole;

    final private WarehouseRepository warehouseRepository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadWarehouses();
        AuthenticatedUser user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getName());
            userRole.setText(user.getUserType().getDbValue());
        }
    }

    public WarehouseController() {
        this.warehouseRepository = WarehouseRepository.getInstance();
    }

    @FXML
    private void goToAddWarehouse(ActionEvent event) throws IOException {
        Parent root = SceneManager.loadFxml(View.WAREHOUSE_CREATE);
        Stage stage = StageManager.getInstance().getMainStage();
        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
        StageManager.getInstance().setScene(scene, "Add Warehouse");

    }

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
        Label initials = new Label(getInitials(warehouse.getName()));
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
        VBox staffBox = new VBox(4,
                styledLabel("24", 28, true, "warehouse-stat-value"),
                styledLabel("Staff", 12, false, "warehouse-stat-label")
        );
        staffBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(staffBox, Priority.ALWAYS);

        VBox stockBox = new VBox(4,
                styledLabel("4,523", 28, true, "warehouse-stat-value"),
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
                    (WarehouseDashboardController controller) -> controller.setWarehouse(warehouse)
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

    private String getInitials(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        } else if (parts.length == 1 && parts[0].length() > 0) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return "W";
    }
}
