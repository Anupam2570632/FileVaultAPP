package com.example.loginreg;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

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
    private TextArea fileNameBox;

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
        categoryDropdown.setItems(FXCollections.observableArrayList("Code", "JSON", "Text"));
        validityDropdown.setItems(FXCollections.observableArrayList("1 Month", "1 Year"));
        accessDropdown.setItems(FXCollections.observableArrayList("Public", "Private"));

        savedFiles = FXCollections.observableArrayList();
        savedFilesList.setItems(savedFiles);

        loadSavedFiles();

        createFileButton.setOnAction(event -> handleCreateFile());
    }

    private void handleCreateFile() {
        String category = categoryDropdown.getValue();
        String validity = validityDropdown.getValue();
        String access = accessDropdown.getValue();
        String content = codeSpace.getText();
        String fileName = fileNameBox.getText();

        if (category == null || validity == null || access == null || fileName == null || content.isEmpty()) {
            showAlert("Error", "All fields must be filled out!", Alert.AlertType.ERROR);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("category", category);
        jsonObject.put("validity", validity);
        jsonObject.put("access", access);
        jsonObject.put("fileName", fileNameBox.getText());
        jsonObject.put("content", content);
        jsonObject.put("email", UserSession.getUserEmail());

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3000/file"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                System.out.println("File Saved Successfully: " + response.body());
                codeSpace.clear();
                fileNameBox.clear();
                loadSavedFiles(); // Reload files after saving
            } else {
                System.out.println("Error during saving file: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSavedFiles() {
        String email = UserSession.getUserEmail();

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3000/file?email=" + email))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            savedFiles.clear(); // Clear previous list

            if (response.statusCode() == 200) {
                JSONArray filesArray = new JSONArray(response.body());

                if (filesArray.length() == 0) {
                    updateSavedFilesUI(); // No files found
                    return;
                }

                for (int i = 0; i < filesArray.length(); i++) {
                    JSONObject file = filesArray.getJSONObject(i);
                    String id = file.optString("_id");
                    String fileName = file.optString("fileName", "Untitled");
                    String content = file.optString("content", "");
                    addSavedFile(id, fileName, content);
                }
            } else {
                System.out.println("Failed to load files: " + response.body());
            }

            updateSavedFilesUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSavedFile(String id, String fileName, String content) {
        HBox fileEntry = new HBox(20);
        fileEntry.setStyle("-fx-padding: 10px; -fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        Label fileNameLabel = new Label("📄 " + fileName);
        fileNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #000000;");

        String previewContent = content.length() > 30 ? content.substring(0, 30) + "..." : content;
        Label previewLabel = new Label(previewContent);
        previewLabel.setStyle("-fx-text-fill: #555;");

        VBox fileInfoBox = new VBox(5, fileNameLabel, previewLabel);

        // Spacer to push buttons to right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action buttons
        Button seeButton = new Button("See");
        Button editButton = new Button("Edit");
        Button copyButton = new Button("Copy");
        Button downloadButton = new Button("Download");
        Button deleteButton = new Button("Delete");

        seeButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        copyButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        downloadButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(5, seeButton, editButton, copyButton, downloadButton, deleteButton);

        fileEntry.getChildren().addAll(fileInfoBox, spacer, buttonBox);

        // Event handlers
        seeButton.setOnAction(event -> handleSeeFile(content));
        editButton.setOnAction(event -> handleEditFile(id));
        copyButton.setOnAction(event -> handleCopyFile(content));
        downloadButton.setOnAction(event -> handleDownloadFile(fileName, content));
        deleteButton.setOnAction(event -> handleDeleteFile(id));

        savedFiles.add(fileEntry);
        updateSavedFilesUI();
    }


    private void handleSeeFile(String content) {
        showAlert("File Content", content, Alert.AlertType.INFORMATION);
    }

    private void handleEditFile(String id) {
        UserSession.setUpdateId(id);
        Main.showUpdateFIleScene();
    }

    private void handleCopyFile(String content) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        clipboard.setContent(clipboardContent);

        showAlert("Copy File", "File content copied to clipboard!", Alert.AlertType.INFORMATION);
    }


    private void handleDownloadFile(String fileName, String content) {
        try {
            String userHome = System.getProperty("user.home");
            String downloadPath = userHome + "/Downloads/" + fileName + ".txt";

            java.nio.file.Files.writeString(java.nio.file.Paths.get(downloadPath), content);

            showAlert("Download Success", "File downloaded as: " + downloadPath, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Download Error", "Something went wrong while downloading the file.", Alert.AlertType.ERROR);
        }
    }


    private void handleDeleteFile(String id) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Confirmation");
        confirmation.setHeaderText("Are you sure you want to delete this file?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            // API URL
            String url = "http://localhost:3000/delete-file/" + id;

            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .DELETE()
                        .build();

                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            if (response.statusCode() == 200) {
                                Platform.runLater(() -> {
                                    savedFiles.removeIf(fileEntry -> {
                                        Label label = (Label) ((VBox) fileEntry.getChildren().get(0)).getChildren().get(0);
                                        return label.getText().replace("📄 ", "").equals(id);
                                    });
                                    updateSavedFilesUI();
                                    showAlert("Delete Success", "File deleted from server.", Alert.AlertType.INFORMATION);
                                });
                            } else if (response.statusCode() == 404) {
                                Platform.runLater(() -> showAlert("Not Found", "File not found on server.", Alert.AlertType.WARNING));
                            } else {
                                Platform.runLater(() -> showAlert("Error", "Server returned error while deleting.", Alert.AlertType.ERROR));
                            }
                        }).exceptionally(e -> {
                            e.printStackTrace();
                            Platform.runLater(() -> showAlert("Network Error", "Failed to contact server.", Alert.AlertType.ERROR));
                            return null;
                        });

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Something went wrong while deleting from server.", Alert.AlertType.ERROR);
            }
        }
    }


    private void updateSavedFilesUI() {
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
