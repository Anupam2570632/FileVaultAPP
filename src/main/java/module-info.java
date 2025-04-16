module com.example.loginreg {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;
    requires com.fasterxml.jackson.databind;


    opens com.example.loginreg to javafx.fxml;
    exports com.example.loginreg;
}