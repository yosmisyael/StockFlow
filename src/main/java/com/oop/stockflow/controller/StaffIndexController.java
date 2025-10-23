package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Staff;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.StaffRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

import static com.oop.stockflow.app.View.STAFF_CREATE;

public class StaffIndexController {
    private Warehouse currentWarehouse;
    private AuthenticatedUser currentUser;

    private final StaffRepository staffRepository =  StaffRepository.getInstance();

    @FXML
    private VBox staffListContainer;
    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label initialLabel;
    @FXML
    private Label warehouseLabel;
    @FXML
    private Label totalStaffLabel;

    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;
        loadPageContext();
        loadStaffList(warehouse.getId());
    }

    // navigations
    @FXML
    private void goToCreateStaff() {
        StageManager.getInstance().navigateWithData(
                STAFF_CREATE,
                "Add Warehouse " + currentWarehouse.getId() + " Staff",
                (StaffCreateController controller) -> { controller.initData(currentWarehouse, currentUser); }
        );
    }

    @FXML
    private void goToWarehouseDashboard() {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_DASHBOARD,
                "Warehouse " + currentWarehouse + " Dashboard",
                (WarehouseDashboardController controller) -> { controller.initData(currentWarehouse, currentUser); }
        );
    }

    @FXML
    private void goToProductIndex() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        StageManager.getInstance().navigateWithData(
                View.PRODUCT_INDEX,
                "Warehouse " + currentWarehouse.getId() + " Product Management",
                (ProductIndexController controller) -> {
                    controller.initData(currentWarehouse, currentUser);
                }
        );
    }

    private void loadStaffList(int warehouseId) {
        if (staffListContainer == null) {
            System.err.println("[ERROR] staffListContainer is null. Check FXML fx:id.");
            return;
        }

        staffListContainer.getChildren().clear();

        List<Staff> staffList = staffRepository.getStaffByWarehouse(warehouseId);

        if (staffList.isEmpty()) {
            Label noStaffLabel = new Label("No staff found for this warehouse.");
            noStaffLabel.setStyle("-fx-text-fill: #6b7280; -fx-padding: 20;");
            staffListContainer.getChildren().add(noStaffLabel);
        } else {
            int staffCount = staffList.size();
            totalStaffLabel.setText(Integer.toString(staffCount));
            warehouseLabel.setText("Active staff in warehouse " + currentWarehouse.getName());
            for (Staff staff : staffList) {
                HBox staffCard = createStaffCard(staff);
                staffListContainer.getChildren().add(staffCard);
            }
        }
    }

    private HBox createStaffCard(Staff staff) {
        HBox card = new HBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(16);
        card.getStyleClass().add("staff-item");
        card.setPadding(new Insets(16, 16, 16, 16));

        // Avatar
        Label avatarLabel = new Label("👤");
        avatarLabel.getStyleClass().add("staff-avatar");
        avatarLabel.setFont(Font.font(32));

        // staff info
        Label nameLabel = new Label(staff.getName());
        nameLabel.getStyleClass().add("staff-name");
        nameLabel.setFont(Font.font("System Bold", 16));
        String formattedId = String.format("STF%03d", staff.getId());
        Label idLabel = new Label("ID: " + formattedId);
        idLabel.getStyleClass().add("staff-detail");
        idLabel.setFont(Font.font(15));
        Label emailLabel = new Label("Email: " + staff.getEmail());
        emailLabel.getStyleClass().add("staff-detail");
        emailLabel.setFont(Font.font(15));
        HBox detailBox = new HBox(16, idLabel, emailLabel);
        VBox infoBox = new VBox(4, nameLabel, detailBox);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // edit button
        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("action-button-edit");
        editButton.setOnAction(event -> handleEditStaff(staff));

        // delete button
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("action-button-delete");
        deleteButton.setOnAction(event -> handleDeleteStaff(staff));

        // grouping buttons
        HBox actionButtons = new HBox(8, editButton, deleteButton);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(avatarLabel, infoBox, actionButtons);

        return card;
    }

    // action handlers
    private void handleEditStaff(Staff staff) {
        StageManager.getInstance().navigateWithData(
            View.STAFF_EDIT,
        "Edit Staff Information",
            (StaffEditController controller) -> { controller.initData(currentWarehouse, currentUser, staff);}
        );
    }

    private void handleDeleteStaff(Staff staff) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Staff Account?");
        confirmation.setContentText("Are you sure you want to delete the staff account for '" + staff.getName() + "'? This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = staffRepository.deleteStaff(staff.getId());
            if (deleted) {
                // rerender staff list
                showAlert(Alert.AlertType.INFORMATION, "Success", "Staff account for '" + staff.getName() + "' has been deleted.");
                loadStaffList(this.currentWarehouse.getId());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete staff account.");
            }
        } else {
            System.out.println("Deletion cancelled for staff ID: " + staff.getId());
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
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
