package com.example.loginreg;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertController {

    // Method to show an alert with title, message, and alert type
    public static void showAlert(String title, String message, AlertType alertType) {
        // Create the alert with the provided type
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);  // Optional: Set header text, can be null if not needed
        alert.setContentText(message);

        // Show the alert and wait for user response
        alert.showAndWait();
    }
}
