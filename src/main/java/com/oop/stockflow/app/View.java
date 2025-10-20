package com.oop.stockflow.app;

public enum View {
    LOGIN("LoginView.fxml"),
    REGISTER("RegisterView.fxml"),
    DASHBOARD("MainDashboardView.fxml"),
    PRODUCT_LIST("ProductListView.fxml"),
    WAREHOUSE_LIST("WarehouseView.fxml"),
    WAREHOUSE_ADD("WarehouseCreate.fxml"),
    WAREHOUSE_DASHBOARD("WarehouseDashboard.fxml"),;

    private final String fxmlFile;

    View(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    public String getFxmlFile() {
        return "/com/oop/stockflow/view/" + fxmlFile;
    }
}
