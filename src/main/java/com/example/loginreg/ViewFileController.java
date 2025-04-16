package com.example.loginreg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ViewFileController {

    @FXML
    private Label fileNameLabel;

    @FXML
    private TextArea fileContentArea;

    @FXML
    private Button backButton;

    public VBox getView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("viewFile.fxml"));
            loader.setController(this);
            VBox root = loader.load();
            getFileData(); // fetch file data after view is loaded
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void initialize() {
        backButton.setOnAction(e -> handleBack());
    }

    public void setFileDetails(String fileName, String content) {
        fileNameLabel.setText("File: " + fileName);
        fileContentArea.setText(content);
    }

    @FXML
    private void handleBack() {
        Main.showAddFileScene();
    }

    private void getFileData() {
        try {
            String id = UserSession.getViewFileId(); // get file id from session
            String url = "https://file-vault-server-eight.vercel.app/file/" + id;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(responseBody);

                String fileName = jsonNode.get("fileName").asText();
                String content = jsonNode.get("content").asText();

                setFileDetails(fileName, content);
            } else {
                System.out.println("Failed to fetch file. Status: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
