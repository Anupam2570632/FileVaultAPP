package com.example.loginreg;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;

public class ViewFileController {

    @FXML
    private Label fileNameLabel;

    @FXML
    private TextArea fileContentArea;

    public VBox getView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("viewFile.fxml"));
            loader.setController(this);
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setFileDetails(String fileName, String content) {
        fileNameLabel.setText("File: " + fileName);
        fileContentArea.setText(content);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Main.showAddFileScene();
    }
}
