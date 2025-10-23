package com.oop.stockflow.app;

public enum View {
    LOGIN("LoginView.fxml"),
    REGISTER("RegisterView.fxml"),
    PRODUCT_INDEX("ProductIndex.fxml"),
    PRODUCT_CREATE("ProductCreate.fxml"),
    PRODUCT_EDIT("ProductEdit.fxml"),
    PRODUCT_SHOW("ProductShow.fxml"),
    STAFF_INDEX("StaffIndex.fxml"),
    STAFF_CREATE("StaffCreate.fxml"),
    STAFF_EDIT("StaffEdit.fxml"),
    STAFF_SETTINGS("StaffSettings.fxml"),
    TRANSACTION_INDEX("TransactionIndex.fxml"),
    TRANSACTION_SHOW("TransactionShow.fxml"),
    TRANSACTION_CREATE_OUTBOUND("OutboundTransaction.fxml"),
    TRANSACTION_CREATE_INBOUND("InboundTransaction.fxml"),
    WAREHOUSE_INDEX("WarehouseIndex.fxml"),
    WAREHOUSE_SHOW("WarehouseShow.fxml"),
    WAREHOUSE_CREATE("WarehouseCreate.fxml"),
    WAREHOUSE_EDIT("WarehouseEdit.fxml");

    private final String fxmlFile;

    View(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    public String getFxmlFile() {
        return "/com/oop/stockflow/view/" + fxmlFile;
    }
}
