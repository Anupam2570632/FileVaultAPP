package com.example.loginreg;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RegisterController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField emailField;
    @FXML
    private Button registerButton;
    @FXML
    private Button navigateToLoginButton;

    public VBox getView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
            loader.setController(this);
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void initialize() {
        navigateToLoginButton.setOnAction(e -> handleRegisterNavigation());
        registerButton.setOnAction(e -> handleRegister());
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (validateRegistration(username, email, password, confirmPassword)) {
            // Perform API call for registration
            try {
                sendPostRequest(username, email, password);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error while registering user: " + e.getMessage());
            }
        }
    }

    private boolean validateRegistration(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            AlertController.showAlert("Information", "Fields cannot be empty!", AlertType.ERROR);
            return false;
        }
        if (!isEmailValid(email)) {
            AlertController.showAlert("Information", "Invalid email format!", AlertType.ERROR);
            return false;
        }
        if (!password.equals(confirmPassword)) {
            AlertController.showAlert("Information", "Passwords do not match!", AlertType.ERROR);
            return false;
        }
        if (!isPasswordStrong(password)) {
            AlertController.showAlert("Information", "Password must be at least 8 characters long, contain one uppercase letter, one special character, and one number!", AlertType.ERROR);
            return false;
        }
        return true;
    }

    private boolean isPasswordStrong(String password) {
        // Check for minimum 8 characters, at least one uppercase letter, one special character, and one number
        String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*\\d).{8,}$";
        return password.matches(passwordPattern);
    }

    private boolean isEmailValid(String email) {
        // Check for a valid email format
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }

    private void sendPostRequest(String username, String email, String passCode) throws IOException, InterruptedException {
        // Create JSON object for request body
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("email", email);
        jsonObject.put("passCode", passCode);

        // Create HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Build HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/user")) // Replace with your API endpoint
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                .build();

        // Send request and handle response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            System.out.println("User registered successfully: " + response.body());
            Main.showLoginScene();
        } else {
            System.out.println("Error during registration: " + response.body());
        }
    }

    private void handleRegisterNavigation() {
        Main.showLoginScene();
    }
}
