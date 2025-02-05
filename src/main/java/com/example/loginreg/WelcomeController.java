package com.example.loginreg;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


public class WelcomeController {
    @FXML
    private Label welcomeLabel;

    public VBox getView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("welcome.fxml"));
            loader.setController(this);
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void initialize() {
        welcomeLabel.setText("Welcome to the application!");
    }
}