package com.example.loginreg;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML private Button registerButton;

    public VBox getView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            loader.setController(this);
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void initialize() {
        loginButton.setOnAction(e -> {
            try {
                handleLogin();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        registerButton.setOnAction(e -> handleRegisterNavigation());
    }

    private void handleLogin() throws IOException, InterruptedException {
        String email = emailField.getText();
        String password = passwordField.getText();

        if(validateLogin(email, password)) {
            sentGetRequest(email, password);
        }
    }

    private boolean validateLogin(String email, String password) {
        if (password.isEmpty() || email.isEmpty()) {
            AlertController.showAlert("Information", "Fields cannot be empty!", Alert.AlertType.ERROR);
            return false;
        }
        if (!isEmailValid(email)) {
            AlertController.showAlert("Information", "Invalid email format!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        // Check for a valid email format
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }

    private void sentGetRequest(String email, String password) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String url = String.format("https://file-vault-server-eight.vercel.app/user?email=%s&passcode=%s",
                URLEncoder.encode(email, StandardCharsets.UTF_8),
                URLEncoder.encode(password, StandardCharsets.UTF_8));

        // Create HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Send request and handle response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("User Login successfully: " + response.body());
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONObject userObject = jsonResponse.getJSONObject("user");

            // Directly assign to UserSession without setter
            UserSession.setUserEmail(userObject.getString("email"));
            System.out.println(UserSession.getUserEmail());
            Main.showAddFileScene();
        } else if(response.statusCode() == 401) {
            AlertController.showAlert("Information", "Incorrect Password", Alert.AlertType.ERROR);
        }else if(response.statusCode()==500){
            AlertController.showAlert("Information", "Internal server error", Alert.AlertType.ERROR);
        } else{
            System.out.println("Error during Login: " + response.body());
        }
    }


    private void handleRegisterNavigation() {
        Main.showRegisterScene();
    }
}