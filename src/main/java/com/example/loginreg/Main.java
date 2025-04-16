package com.example.loginreg;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage primaryStage;
    private static LoginController loginController;
    private static RegisterController registerController;
    private static WelcomeController welcomeController;
    private static AddFileController addFileController;
    private static UpdateFileController updateFileController;
    private static ViewFileController viewFileController;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("JavaFX Application");

        // Initialize controllers
        loginController = new LoginController();
        registerController = new RegisterController();
        welcomeController = new WelcomeController();
        addFileController = new AddFileController();
        updateFileController = new UpdateFileController();
        viewFileController = new ViewFileController();

        // Show login scene initially
        showLoginScene();

        primaryStage.show();
    }

    public static void showLoginScene() {
        VBox loginView = loginController.getView();
        if (loginView != null) {
            Scene scene = new Scene(loginView, 400, 300);
            primaryStage.setScene(scene);
        }
    }

    public static void showRegisterScene() {
        VBox registerView = registerController.getView();
        if (registerView != null) {
            Scene scene = new Scene(registerView, 400, 400);
            primaryStage.setScene(scene);
        }
    }

    public static void showWelcomeScene() {
        VBox welcomeView = welcomeController.getView();
        if (welcomeView != null) {
            Scene scene = new Scene(welcomeView, 600, 400);
            primaryStage.setScene(scene);
        }
    }
    public static void showAddFileScene() {
        VBox addFileView = addFileController.getView();
        if (addFileView != null) {
            Scene scene = new Scene(addFileView, 700, 500);
            primaryStage.setScene(scene);
        }
    }
    public static void showUpdateFIleScene() {
        VBox updateFileView = updateFileController.getView();
        if (updateFileView != null) {
            Scene scene = new Scene(updateFileView, 600, 600);
            primaryStage.setScene(scene);
        }
    }

    public static void showViewFileScene(){
        VBox viewFile = viewFileController.getView();
        if(viewFile != null){
            Scene scene = new Scene(viewFile, 600, 600);
            primaryStage.setScene(scene);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
