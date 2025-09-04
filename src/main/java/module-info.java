module com.example.cads2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.poi.ooxml;
    requires kernel;
    requires layout;
    requires io;


    opens com.example.cads2 to javafx.fxml;
    exports com.example.cads2;
}