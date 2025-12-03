package com.pos.controller;

import com.pos.models.UserSession;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label currentTimeLabel;
    @FXML private Label currentDateLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label todaySalesLabel;
    @FXML private Label transactionsCountLabel;
    @FXML private Label topCategoryLabel;
    @FXML private Label lowStockLabel;

    @FXML private TableView<Transaction> recentTransactionsTable;
    @FXML private TableColumn<Transaction, String> colTransactionId;
    @FXML private TableColumn<Transaction, String> colTime;
    @FXML private TableColumn<Transaction, String> colAmount;
    @FXML private TableColumn<Transaction, String> colCashier;
    @FXML private TableColumn<Transaction, String> colStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupClock();
        setupWelcomeMessage();
        setupDashboardCards();
        setupRecentTransactionsTable();
        loadDummyData();
    }

    private void setupClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            currentTimeLabel.setText(timeFormat.format(new Date()));
            currentDateLabel.setText(dateFormat.format(new Date()));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }


    private void setupWelcomeMessage() {
        String[] greetings = {"Good morning", "Good afternoon", "Good evening"};
        int hour = java.time.LocalTime.now().getHour();
        String greeting = hour < 12 ? greetings[0] : hour < 18 ? greetings[1] : greetings[2];


        welcomeLabel.setText(greeting + " " + UserSession.getInstance().getFullName());
    }

    private void setupDashboardCards() {
        // Cards are populated with dummy data in loadDummyData()
    }

    private void setupRecentTransactionsTable() {
        colTransactionId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colCashier.setCellValueFactory(new PropertyValueFactory<>("cashier"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadDummyData() {
        // Dashboard Cards Data
        todaySalesLabel.setText("PKR 18,320");
        transactionsCountLabel.setText("32 Orders");
        topCategoryLabel.setText("Beverages");
        lowStockLabel.setText("5 Items");

        // Recent Transactions Data
        recentTransactionsTable.getItems().addAll(
                new Transaction("TXN-001", "14:30", "PKR 1,250", "Hareem", "Paid"),
                new Transaction("TXN-002", "14:15", "PKR 850", "Ali", "Paid"),
                new Transaction("TXN-003", "13:45", "PKR 2,100", "Hareem", "Paid"),
                new Transaction("TXN-004", "13:20", "PKR 450", "Sara", "Cancelled"),
                new Transaction("TXN-005", "12:55", "PKR 1,750", "Hareem", "Paid")
        );
    }

    // Navigation methods
// Navigation methods - ALL CONSISTENT NOW!
    @FXML
    private void showDashboard() {
        // Already on dashboard - maybe refresh data?
        refreshDashboardData();
    }

    @FXML
    private void showTransactions() {
        navigateTo("/com/pos/view/transactions-home.fxml");
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
    private void showReports() {
        navigateTo("/com/pos/view/reports.fxml");
    }

    @FXML
    private void showSettings() {
        navigateTo("/com/pos/view/settings.fxml");
    }

    // Will take a look at this later.
//    @FXML
//    private void logout() {
//        try {
//            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/pos/view/login.fxml"));
//            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
//            Scene currentScene = stage.getScene();
//            currentScene.setRoot(loginRoot);
//            // If login has different CSS, add it here
//            currentScene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
//            stage.setMaximized(false); // Login shouldn't be maximized
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert("Logout Error", "Failed to logout: " + e.getMessage());
//        }
//    }

    // Helper method that WORKS for all navigation
    private void navigateTo(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();

            // Get the EXISTING scene and replace its root - THIS IS THE FIX!
            Scene currentScene = stage.getScene();
            currentScene.setRoot(root);

            // Keep your styles
            currentScene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load page: " + e.getMessage());
        }
    }

    private void refreshDashboardData() {
        // TODO: Refresh dashboard data when staying on dashboard
        System.out.println("Refreshing dashboard data...");
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
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/com/pos/view/login.fxml"));
                        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                        Scene scene = new Scene(root, 1000, 700);
                        scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
                        stage.setTitle("NexusPOS - Login");
                        stage.setScene(scene);
                        stage.setResizable(false);
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
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

    // Transaction data model class
    public static class Transaction {
        private final String transactionId;
        private final String time;
        private final String amount;
        private final String cashier;
        private final String status;

        public Transaction(String transactionId, String time, String amount, String cashier, String status) {
            this.transactionId = transactionId;
            this.time = time;
            this.amount = amount;
            this.cashier = cashier;
            this.status = status;
        }

        public String getTransactionId() { return transactionId; }
        public String getTime() { return time; }
        public String getAmount() { return amount; }
        public String getCashier() { return cashier; }
        public String getStatus() { return status; }
    }
}