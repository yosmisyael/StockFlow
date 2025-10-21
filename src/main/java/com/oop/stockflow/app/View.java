package com.oop.stockflow.app;

public enum View {
    LOGIN("LoginView.fxml"),
    REGISTER("RegisterView.fxml"),
    PRODUCT_INDEX("ProductListView.fxml"),
    WAREHOUSE_INDEX("WarehouseView.fxml"),
    WAREHOUSE_CREATE("WarehouseCreate.fxml"),
    STAFF_INDEX("StaffIndex.fxml"),
    STAFF_CREATE("StaffCreate.fxml"),
    STAFF_EDIT("StaffEdit.fxml"),
    WAREHOUSE_DASHBOARD("WarehouseDashboard.fxml"),;

    private final String fxmlFile;

    View(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    public String getFxmlFile() {
        return "/com/oop/stockflow/view/" + fxmlFile;
    }
}
