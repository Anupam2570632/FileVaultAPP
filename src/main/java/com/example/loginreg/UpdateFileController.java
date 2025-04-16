package com.example.loginreg;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class UpdateFileController {

    @FXML
    private TextField fileNameText;

    @FXML
    private TextField categoryText;

    @FXML
    private TextField validityText;

    @FXML
    private TextField accessText;

    @FXML
    private TextArea fileContentArea;

    @FXML
    private ComboBox<String> categoryDropdown;

    @FXML
    private ComboBox<String> validityDropdown;

    @FXML
    private ComboBox<String> accessDropdown;

    @FXML
    private Button backButton;

    @FXML
    private Button updateButton;

    private Stage stage;

    public VBox getView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("updateFile.fxml"));
            loader.setController(this);
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setFileData(String fileName, String category, String validity, String access, String content) {
        fileNameText.setText(fileName);
        categoryDropdown.setValue(category);
        validityDropdown.setValue(validity);
        accessDropdown.setValue(access);
        fileContentArea.setText(content);
    }

    private void handleBack(ActionEvent event) {
        Main.showAddFileScene();
    }

    private void fetchFileData() {
        String fileId = UserSession.getUpdateId();
        String url = "http://localhost:3000/file/" + fileId;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            // Synchronous request (blocking)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();

            ObjectMapper mapper = new ObjectMapper();
            FileData fileData = mapper.readValue(responseBody, FileData.class);

            setFileData(fileData.getFileName(), fileData.getCategory(), fileData.getValidity(), fileData.getAccess(), fileData.getContent());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        backButton.setOnAction(this::handleBack);
        updateButton.setOnAction(this::handleUpdate); // ✅ Update button event

        // Initialize ComboBox items programmatically
        ObservableList<String> categories = FXCollections.observableArrayList("Code", "JSON", "Text");
        categoryDropdown.setItems(categories);

        ObservableList<String> validityOptions = FXCollections.observableArrayList("1 Month", "1 Year");
        validityDropdown.setItems(validityOptions);

        ObservableList<String> accessOptions = FXCollections.observableArrayList("Public", "Private");
        accessDropdown.setItems(accessOptions);

        fetchFileData();
    }

    private void handleUpdate(ActionEvent event) {
        try {
            // Create FileData object from form inputs
            Map<String, Object> updatedFile = new HashMap<>();
            updatedFile.put("fileName", fileNameText.getText());
            updatedFile.put("category", categoryDropdown.getValue());
            updatedFile.put("validity", validityDropdown.getValue());
            updatedFile.put("access", accessDropdown.getValue());
            updatedFile.put("content", fileContentArea.getText());

            // Convert to JSON string (no _id included)
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(updatedFile);

            // Print JSON to console
            System.out.println("Updated File Data as JSON:");
            System.out.println(json);

            // Get the file ID from session
            String _id = UserSession.getUpdateId();
            String url = "http://localhost:3000/update-file/" + _id;

            // Create HTTP client and PUT request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            // Send request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);

            if (response.statusCode() == 200) {
                Main.showAddFileScene();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
