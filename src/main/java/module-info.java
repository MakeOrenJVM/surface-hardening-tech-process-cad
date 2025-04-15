module com.example.cads2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cads2 to javafx.fxml;
    exports com.example.cads2;
}