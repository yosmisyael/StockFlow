package com.oop.stockflow.app;

public enum View {
    LOGIN("LoginView.fxml"),
    REGISTER("RegisterView.fxml"),
    DASHBOARD("MainDashboardView.fxml"),
    PRODUCT_LIST("ProductListView.fxml");

    private final String fxmlFile;

    View(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    public String getFxmlFile() {
        return "/com/oop/stockflow/view/" + fxmlFile;
    }
}
