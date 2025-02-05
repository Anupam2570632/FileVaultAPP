module com.example.loginreg {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;


    opens com.example.loginreg to javafx.fxml;
    exports com.example.loginreg;
}