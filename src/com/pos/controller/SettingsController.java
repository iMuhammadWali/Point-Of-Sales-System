package com.pos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML private Label currentTimeLabel;
    @FXML private Label currentDateLabel;
    @FXML private Label welcomeLabel;
    @FXML private VBox settingsContent;
    @FXML private Button generalTab;
    @FXML private Button themeTab;
    @FXML private Button printerTab;
    @FXML private Button usersTab;
    @FXML private Button backupTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupClock();
        setupWelcomeMessage();
        showGeneralSettings(); // Default tab
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
        welcomeLabel.setText(greeting + ", Hareem!");
    }

    private void resetTabStyles() {
        generalTab.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-text-fill: #272848; -fx-padding: 12; -fx-alignment: CENTER_LEFT;");
        themeTab.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-text-fill: #272848; -fx-padding: 12; -fx-alignment: CENTER_LEFT;");
        printerTab.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-text-fill: #272848; -fx-padding: 12; -fx-alignment: CENTER_LEFT;");
        usersTab.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-text-fill: #272848; -fx-padding: 12; -fx-alignment: CENTER_LEFT;");
        backupTab.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-text-fill: #272848; -fx-padding: 12; -fx-alignment: CENTER_LEFT;");
    }

    @FXML
    private void showGeneralSettings() {
        resetTabStyles();
        generalTab.setStyle("-fx-background-color: #6D71F9; -fx-background-radius: 8; -fx-text-fill: white; -fx-padding: 12; -fx-alignment: CENTER_LEFT;");

        try {
            VBox generalContent = FXMLLoader.load(getClass().getResource("/com/pos/view/settings-general.fxml"));
            settingsContent.getChildren().setAll(generalContent);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load general settings: " + e.getMessage());
        }
    }

    @FXML
    private void showThemeSettings() {
        resetTabStyles();
        themeTab.setStyle("-fx-background-color: #6D71F9; -fx-background-radius: 8; -fx-text-fill: white; -fx-padding: 12; -fx-alignment: CENTER_LEFT;");

        try {
            VBox themeContent = FXMLLoader.load(getClass().getResource("/com/pos/view/settings-theme.fxml"));
            settingsContent.getChildren().setAll(themeContent);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load theme settings: " + e.getMessage());
        }
    }

    @FXML
    private void showPrinterSettings() {
        resetTabStyles();
        printerTab.setStyle("-fx-background-color: #6D71F9; -fx-background-radius: 8; -fx-text-fill: white; -fx-padding: 12; -fx-alignment: CENTER_LEFT;");

        try {
            VBox printerContent = FXMLLoader.load(getClass().getResource("/com/pos/view/settings-printer.fxml"));
            settingsContent.getChildren().setAll(printerContent);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load printer settings: " + e.getMessage());
        }
    }

    @FXML
    private void showUserSettings() {
        resetTabStyles();
        usersTab.setStyle("-fx-background-color: #6D71F9; -fx-background-radius: 8; -fx-text-fill: white; -fx-padding: 12; -fx-alignment: CENTER_LEFT;");

        try {
            VBox userContent = FXMLLoader.load(getClass().getResource("/com/pos/view/settings-users.fxml"));
            settingsContent.getChildren().setAll(userContent);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load user management: " + e.getMessage());
        }
    }

    @FXML
    private void showBackupSettings() {
        resetTabStyles();
        backupTab.setStyle("-fx-background-color: #6D71F9; -fx-background-radius: 8; -fx-text-fill: white; -fx-padding: 12; -fx-alignment: CENTER_LEFT;");

        try {
            VBox backupContent = FXMLLoader.load(getClass().getResource("/com/pos/view/settings-backup.fxml"));
            settingsContent.getChildren().setAll(backupContent);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load backup settings: " + e.getMessage());
        }
    }

    // Navigation methods
    @FXML
    private void showDashboard() {
        loadScene("/com/pos/view/dashboard.fxml");
    }

    @FXML
    private void showTransactions() {
        loadScene("/com/pos/view/transactions-home.fxml");
    }

    @FXML
    private void showProducts() {
        loadScene("/com/pos/view/products.fxml");
    }

    @FXML
    private void showCustomers() {
        loadScene("/com/pos/view/customers.fxml");
    }

    @FXML
    private void showSettings() {
        // Already on settings page
    }
    @FXML
    private void showReports() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/pos/view/reports.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
            stage.setMaximized(true);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load Reports page: " + e.getMessage());
        }
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
            System.out.println("Attempting to load: " + fxmlPath);
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("Resource not found: " + fxmlPath);
                showAlert("Error", "File not found: " + fxmlPath);
                return;
            }

            Parent root = FXMLLoader.load(resource);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800); // Initial
            stage.setMaximized(true);


            // Load CSS
            URL cssResource = getClass().getResource("/styles/dashboard.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            stage.setScene(scene);

        } catch (Exception e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load: " + fxmlPath + "\nError: " + e.getMessage());
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