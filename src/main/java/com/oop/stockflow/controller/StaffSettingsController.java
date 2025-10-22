package com.oop.stockflow.controller;

import com.oop.stockflow.app.SessionManager;
import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import com.oop.stockflow.model.AuthenticatedUser;
import com.oop.stockflow.model.Warehouse;
import com.oop.stockflow.repository.StaffRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Objects;

public class StaffSettingsController {
    private Warehouse currentWarehouse;
    private AuthenticatedUser currentUser;

    private final StaffRepository staffRepository = StaffRepository.getInstance();

    // Navbar
    @FXML private Label nameLabel;

     @FXML private Label sidebarNameLabel;
     @FXML private Label sidebarRoleLabel;

    // Account Information Card
    @FXML private TextField staffNameField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button togglePasswordButton; // Button containing the eye icon Label

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
            // Handle error appropriately, e.g., navigate back to login
            handleLogout(null); // Force logout
            return;
        }

        // Call methods to populate the UI based on the received data
        loadUserData();
        loadCurrentSettings(); // Placeholder for loading toggle states, etc.
    }

    /**
     * Populates user interface elements with data from the currentUser.
     */
    private void loadUserData() {
        if (currentUser != null) {
            nameLabel.setText(currentUser.getName());
            sidebarNameLabel.setText(currentUser.getName());
            sidebarRoleLabel.setText(currentUser.getUserType().toString());
            staffNameField.setText(currentUser.getName());
        }
    }

    /**
     * Placeholder method to load current settings values (e.g., toggle button states).
     */
    private void loadCurrentSettings() {
        // TODO: Load saved preferences for emailNotificationsToggle and twoFactorToggle
        // Example:
        // boolean emailPref = loadEmailPreference(currentUser.getId());
        // emailNotificationsToggle.setSelected(emailPref);
        // boolean twoFactorPref = loadTwoFactorPreference(currentUser.getId());
        // twoFactorToggle.setSelected(twoFactorPref);

        // For now, use FXML defaults
        System.out.println("Placeholder: Load current settings (toggles).");
    }


    // === Action Handlers ===

    /**
     * Handles the "Save Changes" button click.
     * Validates input and updates staff details (name and optionally password).
     */
    @FXML
    private void handleSaveChanges(ActionEvent event) {
        String newName = staffNameField.getText().trim();
        String newPassword = newPasswordField.getText(); // Don't trim password
        String confirmPassword = confirmPasswordField.getText();

        // --- Validation ---
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

        // --- Update Logic ---
        // Use the flexible update method from StaffRepository
        boolean success = staffRepository.updateStaffDetails(
                currentUser.getId(),
                newName,
                null, // Email is not editable in this form
                passwordChanged ? newPassword : null // Pass null if password isn't changing
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account information updated successfully.");
            // Update currentUser object if name changed (important for UI consistency)
            if (!Objects.equals(currentUser.getName(), newName)) {
                // Assuming AuthenticatedUser has a setter or create a new one for SessionManager
                // currentUser.setName(newName); // If setter exists
                // Reload user data to reflect changes in UI immediately
                loadUserData();
            }
            // Clear password fields after successful save
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
        // Navigate back to the most relevant previous screen, likely Transactions List
        goToTransactionList(event);
    }

    /**
     * Toggles the visibility of the password field content.
     * (Basic implementation - replaces PasswordField text, not secure mask toggling)
     */
    @FXML
    private void togglePasswordVisibility(ActionEvent event) {
        // This is a basic toggle. A more robust solution involves layering
        // a TextField over the PasswordField or using custom controls.
        // For simplicity, we just show/hide the prompt text idea here.

        // Note: JavaFX doesn't have a built-in easy way to show/hide PasswordField text.
        // This handler might just change the eye icon appearance for now.
        System.out.println("Toggle password visibility clicked - complex UI interaction.");
        Label eyeLabel = (Label) togglePasswordButton.getGraphic();
        if (eyeLabel.getText().equals("👁")) {
            eyeLabel.setText("👁‍🗨"); // Example: Change icon to indicate hidden/shown
            // Ideally, you'd replace PasswordField with TextField showing text here
            // and manage the focus. This is complex.
        } else {
            eyeLabel.setText("👁");
            // Change back to PasswordField mask.
        }
        showAlert(Alert.AlertType.INFORMATION,"Info","Password visibility toggle not fully implemented in this basic example.");

    }

    /**
     * Handles changes to the Email Notifications toggle button.
     */
    @FXML
    private void handleEmailNotificationsToggle(ActionEvent event) {
        boolean isSelected = emailNotificationsToggle.isSelected();
        System.out.println("Email Notifications Toggled: " + isSelected);
        // TODO: Save this preference for the currentUser
        // saveEmailPreference(currentUser.getId(), isSelected);
        showAlert(Alert.AlertType.INFORMATION, "Setting Changed", "Email notification preference " + (isSelected ? "enabled." : "disabled."));
    }

    /**
     * Handles changes to the Two-Factor Authentication toggle button.
     */
    @FXML
    private void handleTwoFactorToggle(ActionEvent event) {
        boolean isSelected = twoFactorToggle.isSelected();
        System.out.println("Two-Factor Auth Toggled: " + isSelected);
        // TODO: Implement logic to enable/disable 2FA (might involve more steps)
        // saveTwoFactorPreference(currentUser.getId(), isSelected);
        showAlert(Alert.AlertType.WARNING, "Setting Changed", "Two-Factor Authentication toggled: " + isSelected + "\n(Full 2FA setup not implemented in this example)");
        // Revert toggle if setup fails or isn't implemented
        // if (!setupSuccess) twoFactorToggle.setSelected(!isSelected);
    }

    // navigation
    @FXML
    private void goToTransactionList(ActionEvent event) {
        System.out.println("Navigating to Transactions List...");
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
        System.out.println("Navigating to Outbound Transactions Form...");
        StageManager.getInstance().navigateWithData(
                View.TRANSACTION_CREATE_OUTBOUND,
                "Create Outbound Transactions",
                (OutboundTransactionController controller) -> { controller.initData(currentWarehouse, currentUser); }
        );
    }

    @FXML
    private void goToSettings(ActionEvent event) {
        loadCurrentSettings();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logging out...");
        SessionManager.getInstance().endSession(); // Clear session
    }

    // === Helper Methods ===

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
}