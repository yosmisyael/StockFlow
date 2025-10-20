package com.oop.stockflow.controller;

import com.oop.stockflow.app.StageManager;
import com.oop.stockflow.app.View;
import javafx.fxml.FXML;

public class StaffIndexController {
    @FXML
    private void goToCreateStaff() {
        StageManager.getInstance().navigate(View.STAFF_CREATE, "Add Warehouse Staff");
    }
}
