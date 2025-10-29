package com.oop.stockflow.app;

/**
 * Enumeration representing all available FXML views in the application.
 * Maps view identifiers to their corresponding FXML file names for scene navigation.
 * Each enum constant corresponds to a specific screen or page in the application.
 */
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

    /**
     * Constructs a View enum with its corresponding FXML file name.
     *
     * @param fxmlFile The name of the FXML file (without path prefix).
     */
    View(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    /**
     * Retrieves the full path to the FXML file for this view.
     * Prepends the standard view directory path to the file name.
     *
     * @return The complete resource path to the FXML file (e.g., "/com/oop/stockflow/view/LoginView.fxml").
     */
    public String getFxmlFile() {
        return "/com/oop/stockflow/view/" + fxmlFile;
    }
}
