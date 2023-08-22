module com.example.cs4120finalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.cs4120finalproject to javafx.fxml;
    exports com.example.cs4120finalproject;
}