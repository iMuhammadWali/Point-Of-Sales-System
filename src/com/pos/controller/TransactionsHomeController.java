package com.pos.controller;

import com.pos.models.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionsHomeController {

    @FXML private Label currentTimeLabel;
    @FXML private Label currentDateLabel;
    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        setupClock();
        setupWelcomeMessage();
    }

    private void setupClock() {
        updateTime();
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        }, 0, 1000);
    }

    private void updateTime() {
        javafx.application.Platform.runLater(() -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            currentTimeLabel.setText(timeFormat.format(new Date()));
            currentDateLabel.setText(dateFormat.format(new Date()));
        });
    }

    private void setupWelcomeMessage() {
        String[] greetings = {"Good morning", "Good afternoon", "Good evening"};
        int hour = java.time.LocalTime.now().getHour();
        String greeting = hour < 12 ? greetings[0] : hour < 18 ? greetings[1] : greetings[2];
        welcomeLabel.setText(greeting + " " + UserSession.getInstance().getFullName());
    }

    // Navigation methods
    private void navigateTo(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();

            // GET EXISTING SCENE, DON'T CREATE NEW ONE!
            Scene currentScene = stage.getScene();
            currentScene.setRoot(root);

            currentScene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load page: " + e.getMessage());
        }
    }

    @FXML
    private void showDashboard() {
        navigateTo("/com/pos/view/dashboard.fxml");
    }

    @FXML
    private void showTransactions() {
        // Already on transactions page
    }

    @FXML
    private void showCreateTransaction() {
        navigateTo("/com/pos/view/transactions-create.fxml");
    }

    @FXML
    private void showTransactionHistory() {
        navigateTo("/com/pos/view/transactions-history.fxml");
    }

    @FXML
    private void showProducts() {
        navigateTo("/com/pos/view/products.fxml");
    }

    @FXML
    private void showCustomers() {
        navigateTo("/com/pos/view/customers.fxml");
    }

    @FXML
    private void showSettings() {
        // navigateTo("/com/pos/view/settings.fxml");
    }

    @FXML
    private void showReports() {
        navigateTo("/com/pos/view/reports.fxml"); // FIXED!
    }

    @FXML
    private void logout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText("Confirm Logout");
            alert.setContentText("Are you sure you want to logout?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    loadScene("/com/pos/view/login.fxml");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root); // no fixed size
            scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}