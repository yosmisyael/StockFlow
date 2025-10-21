package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Staff;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.StaffRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class StaffEditController implements Initializable {
    private Staff staffToEdit;
    private Warehouse currentWarehouse;

    private StaffRepository staffRepository = StaffRepository.getInstance();

    @FXML private Label userNameLabel;
    @FXML private Label userRole;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserData();
    }

    public void initData(Warehouse warehouse, Staff staff) {
        this.currentWarehouse = warehouse;
        this.staffToEdit = staff;

        if (staffToEdit == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Staff data not received. Cannot edit.");
            // Mungkin navigasi kembali
            handleCancel();
            return;
        }

        // Isi form dengan data yang ada
        populateForm();
    }

    /**
     * Mengisi field form dengan data dari staffToEdit.
     */
    private void populateForm() {
        if (staffToEdit != null) {
            nameField.setText(staffToEdit.getName());
            emailField.setText(staffToEdit.getEmail());
            // Password DIBIARKAN KOSONG secara default
            passwordField.setPromptText("Leave blank to keep current password");
        }
    }

    /**
     * Aksi saat tombol "Save Changes" ditekan.
     */
    @FXML
    private void handleUpdateStaff() {
        if (staffToEdit == null) {
            showAlert(Alert.AlertType.ERROR, "System Error", "No staff data available to update.");
            return;
        }

        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPassword = passwordField.getText();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Name and Email fields are required.");
            return;
        }

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!Pattern.matches(emailRegex, newEmail)) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Email address format is invalid.");
            return;
        }

        boolean updatePassword = !newPassword.isEmpty();
        if (updatePassword && newPassword.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "New password is too short (minimum 8 characters).");
            return;
        }

        staffToEdit.setName(newName);
        staffToEdit.setEmail(newEmail);

        boolean success = staffRepository.updateStaffDetails(
                staffToEdit.getId(),
                newName,
                newEmail,
                newPassword
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Staff details updated successfully!");
            goToStaffMenu();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update staff details. Email might already exist.");
        }
    }

    /**
     * Memuat data pengguna yang login di sidebar.
     */
    private void loadUserData() {
        AuthenticatedUser user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getName());
            userRole.setText(user.getUserType().toString());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goToDashboard() {
        StageManager.getInstance().navigateWithData(
            View.WAREHOUSE_DASHBOARD,
            "Dashboard",
            (WarehouseDashboardController controller) -> { controller.setWarehouse(currentWarehouse); }
        );
    }

    @FXML
    private void goToStaffMenu() {
        StageManager.getInstance().navigateWithData(
                View.STAFF_INDEX,
                "Staff Management",
                (StaffIndexController controller) -> { controller.initData(currentWarehouse); }
        );
    }

    @FXML
    private void handleCancel() {
        goToStaffMenu();
    }

    @FXML
    private void handleLogout() {
    }
}
