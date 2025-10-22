package com.oop.stockflow.app;

public enum View {
    LOGIN("LoginView.fxml"),
    REGISTER("RegisterView.fxml"),
    PRODUCT_INDEX("ProductIndex.fxml"),
    PRODUCT_CREATE("ProductCreate.fxml"),
    PRODUCT_EDIT("ProductEdit.fxml"),
    PRODUCT_SHOW("ProductShow.fxml"),
    WAREHOUSE_INDEX("WarehouseView.fxml"),
    WAREHOUSE_CREATE("WarehouseCreate.fxml"),
    STAFF_INDEX("StaffIndex.fxml"),
    STAFF_CREATE("StaffCreate.fxml"),
    STAFF_EDIT("StaffEdit.fxml"),
    STAFF_SETTINGS("StaffSettings.fxml"),
    TRANSACTION_INDEX("TransactionIndex.fxml"),
    TRANSACTION_CREATE_OUTBOUND("OutboundTransaction.fxml"),
    TRANSACTION_CREATE_INBOUND("InboundTransaction.fxml"),
    WAREHOUSE_DASHBOARD("WarehouseDashboard.fxml");

    private final String fxmlFile;

    View(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    public String getFxmlFile() {
        return "/com/oop/stockflow/view/" + fxmlFile;
    }
}
