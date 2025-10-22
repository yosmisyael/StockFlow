module com.oop.stockflow {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;
    requires jbcrypt;
    requires jdk.xml.dom;
    requires java.dotenv;
    requires com.zaxxer.hikari;
    requires java.desktop;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    opens com.oop.stockflow to javafx.fxml;
    exports com.oop.stockflow;
}