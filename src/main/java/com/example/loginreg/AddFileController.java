package com.example.loginreg;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AddFileController {

    @FXML
    private ComboBox<String> categoryDropdown;

    @FXML
    private ComboBox<String> validityDropdown;

    @FXML
    private ComboBox<String> accessDropdown;

    @FXML
    private Button createFileButton;

    @FXML
    private TextArea codeSpace;

    @FXML
    private Text noSavedFileText;

    @FXML
    private ListView<HBox> savedFilesList;

    private ObservableList<HBox> savedFiles;

    public VBox getView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addFile.fxml"));
            loader.setController(this);
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void initialize() {
        // Initialize dropdown options
        categoryDropdown.setItems(FXCollections.observableArrayList("Code", "JSON", "Text"));
        validityDropdown.setItems(FXCollections.observableArrayList("1 Month", "1 Year"));
        accessDropdown.setItems(FXCollections.observableArrayList("Public", "Private"));

        // Initialize saved files list
        savedFiles = FXCollections.observableArrayList();
        savedFilesList.setItems(savedFiles);

        // Update UI to reflect no saved files initially
        updateSavedFilesUI();

        // Event listeners
        createFileButton.setOnAction(event -> handleCreateFile());
    }

    private void handleCreateFile() {
        // Collect data from dropdowns and code space
        String category = categoryDropdown.getValue();
        String validity = validityDropdown.getValue();
        String access = accessDropdown.getValue();
        String content = codeSpace.getText();

        // Validate user input
        if (category == null || validity == null || access == null || content.isEmpty()) {
            showAlert("Error", "All fields must be filled out!", Alert.AlertType.ERROR);
            return;
        }

        // Simulate saving the file (this could be connected to a backend or database)
        addSavedFile("New File", content);

        // Clear the code space
        codeSpace.clear();
    }

    private void addSavedFile(String fileName, String content) {
        // Create an HBox for the saved file entry
        HBox fileEntry = new HBox(10);

        Label fileNameLabel = new Label(fileName);
        Button seeButton = new Button("See");
        Button editButton = new Button("Edit");
        Button copyButton = new Button("Copy");
        Button downloadButton = new Button("Download");
        Button deleteButton = new Button("Delete");

        fileEntry.getChildren().addAll(fileNameLabel, seeButton, editButton, copyButton, downloadButton, deleteButton);

        // Add functionality to buttons (placeholder actions for now)
        seeButton.setOnAction(event -> handleSeeFile(content));
        editButton.setOnAction(event -> handleEditFile(fileName, content));
        copyButton.setOnAction(event -> handleCopyFile(content));
        downloadButton.setOnAction(event -> handleDownloadFile(fileName));
        deleteButton.setOnAction(event -> handleDeleteFile(fileEntry));

        // Add the entry to the saved files list
        savedFiles.add(fileEntry);
        updateSavedFilesUI();
    }

    private void handleSeeFile(String content) {
        // Display the content in a dialog or new window
        showAlert("File Content", content, Alert.AlertType.INFORMATION);
    }

    private void handleEditFile(String fileName, String content) {
        // Pre-fill the code space with the content for editing
        codeSpace.setText(content);
        showAlert("Edit File", "Editing file: " + fileName, Alert.AlertType.INFORMATION);
    }

    private void handleCopyFile(String content) {
        // Copy content to clipboard (placeholder logic)
        showAlert("Copy File", "File content copied to clipboard!", Alert.AlertType.INFORMATION);
    }

    private void handleDownloadFile(String fileName) {
        // Simulate file download (placeholder logic)
        showAlert("Download File", "File \"" + fileName + "\" downloaded!", Alert.AlertType.INFORMATION);
    }

    private void handleDeleteFile(HBox fileEntry) {
        // Remove the file entry from the list
        savedFiles.remove(fileEntry);
        updateSavedFilesUI();
    }

    private void updateSavedFilesUI() {
        // Toggle visibility of "No saved files" text and file list
        boolean hasFiles = !savedFiles.isEmpty();
        noSavedFileText.setVisible(!hasFiles);
        savedFilesList.setVisible(hasFiles);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
