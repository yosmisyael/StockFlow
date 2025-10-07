module com.oop.stockflow {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;


    opens com.oop.stockflow to javafx.fxml;
    exports com.oop.stockflow;
}