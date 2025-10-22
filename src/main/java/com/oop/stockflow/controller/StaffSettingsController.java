package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Staff;
import com.oop.stockflow.model.UserType;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.StaffRepository;
import com.oop.stockflow.utils.DateTimeUtils;
import com.oop.stockflow.utils.StringUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class StaffSettingsController {
    private Warehouse currentWarehouse;
    private AuthenticatedUser currentUser;

    private final StaffRepository staffRepository = StaffRepository.getInstance();

    // Navbar
     @FXML private Label nameLabel;
     @FXML private Label roleLabel;
     @FXML private Label dateLabel;
     @FXML private Label initialLabel;


    // Account Information Card
    @FXML private TextField staffNameField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button togglePasswordButton;

    // Additional Settings Card
    @FXML private ToggleButton emailNotificationsToggle;
    @FXML private ToggleButton twoFactorToggle;

    /**
     * Public method called after FXML loading to pass initial data.
     * This replaces the Initializable interface for data loading.
     *
     * @param warehouse The current warehouse context (can be null if not strictly needed).
     * @param user      The currently logged-in authenticated user.
     */
    public void initData(Warehouse warehouse, AuthenticatedUser user) {
        this.currentWarehouse = warehouse;
        this.currentUser = user;

        if (this.currentUser == null) {
            System.err.println("FATAL ERROR: StaffSettingsController requires a valid AuthenticatedUser.");
            return;
        }
        loadPageContext();
        loadUserData();
    }

    /**
     * Populates user interface elements with data from the currentUser.
     */
    private void loadUserData() {
        if (currentUser != null) {
            Staff staff = StaffRepository.getInstance().getStaffById(currentUser.getId());
            currentUser = new AuthenticatedUser(staff.getId(), staff.getName(), UserType.STAFF);
            nameLabel.setText(currentUser.getName());
            roleLabel.setText(currentUser.getUserType().getDbValue());
            staffNameField.setText(currentUser.getName());
        }
    }


    // action handlers
    /**
     * Handles the "Save Changes" button click.
     * Validates input and updates staff details (name and optionally password).
     */
    @FXML
    private void handleSaveChanges(ActionEvent event) {
        String newName = staffNameField.getText().trim();
        String newPassword = newPasswordField.getText(); // Don't trim password
        String confirmPassword = confirmPasswordField.getText();

        if (newName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Staff Name cannot be empty.");
            return;
        }

        boolean passwordChanged = !newPassword.isEmpty();
        if (passwordChanged) {
            if (newPassword.length() < 8) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "New password must be at least 8 characters long.");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "New password and confirmation password do not match.");
                return;
            }
        }

        boolean success = staffRepository.updateStaffDetails(
                currentUser.getId(),
                newName,
                null,
                passwordChanged ? newPassword : null
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account information updated successfully.");
            loadUserData();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update account information. Please try again.");
        }
    }

    /**
     * Handles the "Cancel" button click.
     * Navigates back to the previous screen (e.g., Transactions List).
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        System.out.println("Cancel changes - Navigating back.");
        goToTransactionIndex(event);
    }

    /**
     * Toggles the visibility of the password field content.
     * (Basic implementation - replaces PasswordField text, not secure mask toggling)
     */
    @FXML
    private void togglePasswordVisibility(ActionEvent event) {
        System.out.println("Toggle password visibility clicked - complex UI interaction.");
        Label eyeLabel = (Label) togglePasswordButton.getGraphic();
        if (eyeLabel.getText().equals("👁")) {
            eyeLabel.setText("👁‍🗨");
        } else {
            eyeLabel.setText("👁");
        }
        showAlert(Alert.AlertType.INFORMATION,"Info","Password visibility toggle not fully implemented in this basic example.");

    }

    // navigations
    @FXML
    private void goToTransactionIndex(ActionEvent event) {
        StageManager.getInstance().navigateWithData(
                View.TRANSACTION_INDEX,
                "Transactions List",
                (TransactionIndexController controller) -> { controller.initData(currentWarehouse, currentUser); }
        );
    }

    @FXML
    private void goToInboundTransaction(ActionEvent event) {
        StageManager.getInstance().navigateWithData(
                View.TRANSACTION_CREATE_INBOUND,
                "Create Inbound Transactions",
                (InboundTransactionsController controller) -> { controller.initData(currentWarehouse, currentUser); }
        );
    }

    @FXML
    private void goToOutboundTransaction(ActionEvent event) {
        StageManager.getInstance().navigateWithData(
                View.TRANSACTION_CREATE_OUTBOUND,
                "Create Outbound Transactions",
                (OutboundTransactionController controller) -> { controller.initData(currentWarehouse, currentUser); }
        );
    }

    @FXML
    private void goToSettings() {
        StageManager.getInstance().navigateWithData(
                View.STAFF_SETTINGS,
                "Staff Settings",
                (StaffSettingsController controller) -> { controller.initData(currentWarehouse, currentUser); }
        );
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().endSession();
        StageManager.getInstance().navigate(View.LOGIN, "Login");
    }

    // helper methods
    /**
     * Shows a standard JavaFX Alert dialog.
     */
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