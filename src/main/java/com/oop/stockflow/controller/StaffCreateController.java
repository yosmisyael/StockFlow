package com.oop.stockflow.controller;

import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class StaffCreateController {
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRole;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;


    @FXML
    private void goToDashboard() {
        StageManager.getInstance().navigate(View.WAREHOUSE_DASHBOARD, "Dashboard");
    }

    @FXML
    private void goToStaffMenu() {
        StageManager.getInstance().navigate(View.STAFF_INDEX, "Staff Management");
    }

    @FXML
    private void handleCancel() {
        StageManager.getInstance().navigate(View.STAFF_INDEX, "Warehouse Staff");
    }

    @FXML
    private void handleCreateStaff() {

    }

    @FXML
    private void handleLogout() {
        StageManager.getInstance().navigate(View.STAFF_INDEX, "Warehouse Staff");
    }
}
