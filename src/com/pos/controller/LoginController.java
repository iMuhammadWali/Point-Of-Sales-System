package com.pos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

//import javax.annotation.PostConstruct;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    @FXML
    private void initialize() {
        // Make the login button the default button (pressing Enter anywhere triggers it)
        loginButton.setDefaultButton(true);

        // Also trigger login when Enter is pressed in username or password field
        usernameField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Dummy login check
        if (username.equals("admin") && password.equals("1234")) {
            loadDashboard();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleForgotPassword() {
        // Optional: show alert for now
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Forgot password clicked!");
        alert.showAndWait();
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pos/view/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow(); // get current stage

            Scene scene = new Scene(root);
            // Load CSS for dashboard
            scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());

            stage.setScene(scene);
            stage.setMaximized(true);   // ✅ maximize after login
            stage.setResizable(true);   // make resizable
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load dashboard: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
