package com.oop.stockflow.controller;

import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.Staff;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.StaffRepository;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class StaffIndexController {
    private Warehouse warehouse;

    private final StaffRepository staffRepository =  StaffRepository.getInstance();

    @FXML
    private VBox staffListContainer;

    @FXML
    private void goToCreateStaff() {
        StageManager.getInstance().navigateWithData(
                View.STAFF_CREATE,
                "Add Warehouse " + warehouse.getId() + " Staff",
                (StaffCreateController controller) -> { controller.setWarehouseId(warehouse); }
        );
    }

    @FXML
    private void goToDashboard() {
        StageManager.getInstance().navigateWithData(
                View.WAREHOUSE_DASHBOARD,
                "Warehouse " + warehouse + " Dashboard",
                (WarehouseDashboardController controller) -> { controller.setWarehouse(warehouse); }
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
        card.getStyleClass().add("staff-item"); // Style dari FXML/CSS
        card.setPadding(new javafx.geometry.Insets(16, 16, 16, 16));

        // Avatar
        Label avatarLabel = new Label("👤");
        avatarLabel.getStyleClass().add("staff-avatar");
        avatarLabel.setFont(Font.font(32));

        // Info Staff (Nama, ID, Email/Username)
        Label nameLabel = new Label(staff.getName());
        nameLabel.getStyleClass().add("staff-name");
        nameLabel.setFont(Font.font("System Bold", 16));

        // Format ID Staff (misal: STF001)
        String formattedId = String.format("STF%03d", staff.getId());
        Label idLabel = new Label("ID: " + formattedId);
        idLabel.getStyleClass().add("staff-detail");
        idLabel.setFont(Font.font(13));

        Label emailLabel = new Label("Email: " + staff.getEmail()); // Repo Anda pakai email
        emailLabel.getStyleClass().add("staff-detail");
        emailLabel.setFont(Font.font(13));

        HBox detailBox = new HBox(16, idLabel, emailLabel); // Spasi antara ID dan Email
        VBox infoBox = new VBox(4, nameLabel, detailBox); // Spasi antara Nama dan Detail
        HBox.setHgrow(infoBox, Priority.ALWAYS); // Membuat infoBox mengisi ruang

        // Status Badge (Contoh, Anda perlu logika status dari DB)
        // Repo Anda saat ini tidak mengambil status, perlu ditambahkan jika mau
        // String status = staff.getStatus(); // Asumsi ada getStatus() di model
        String status = "Aktif"; // Placeholder
        Label statusLabel = new Label(status);
        if ("Aktif".equalsIgnoreCase(status)) {
            statusLabel.getStyleClass().add("status-badge-active");
        } else {
            statusLabel.getStyleClass().add("status-badge-leave"); // Atau status lain
        }
        statusLabel.setPadding(new Insets(6, 16, 6, 16));
        statusLabel.setFont(Font.font("System Bold", 12));

        // Menu Button
        Button menuButton = new Button("⋮");
        menuButton.getStyleClass().add("menu-button");
        menuButton.setFont(Font.font(20));
        // Tambahkan aksi untuk menuButton jika perlu
        // menuButton.setOnAction(e -> handleStaffMenu(staff));

        // Gabungkan semua komponen ke dalam kartu HBox
        card.getChildren().addAll(avatarLabel, infoBox, statusLabel, menuButton);

        return card;
    }

    public void initData(Warehouse warehouse) {
        this.warehouse = warehouse;
        loadStaffList(warehouse.getId());
    }
}
