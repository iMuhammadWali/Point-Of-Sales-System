package com.pos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class TransactionHistoryController implements Initializable {

    @FXML private Label currentTimeLabel;
    @FXML private Label currentDateLabel;
    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> colTransactionId;
    @FXML private TableColumn<Transaction, String> colDateTime;
    @FXML private TableColumn<Transaction, String> colCashier;
    @FXML private TableColumn<Transaction, String> colAmount;
    @FXML private TableColumn<Transaction, String> colStatus;

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupClock();
        setupWelcomeMessage();
        setupTransactionsTable();
        loadDummyData();
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

    private void setupTransactionsTable() {
        colTransactionId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colDateTime.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        colCashier.setCellValueFactory(new PropertyValueFactory<>("cashier"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        transactionsTable.setItems(transactions);
    }

    private void loadDummyData() {
        transactions.addAll(
                new Transaction("TXN-001", "2024-01-15 14:30", "Hareem", "PKR 1,250", "Completed"),
                new Transaction("TXN-002", "2024-01-15 14:15", "Ali", "PKR 850", "Completed"),
                new Transaction("TXN-003", "2024-01-15 13:45", "Hareem", "PKR 2,100", "Completed"),
                new Transaction("TXN-004", "2024-01-15 13:20", "Sara", "PKR 450", "Cancelled"),
                new Transaction("TXN-005", "2024-01-15 12:55", "Hareem", "PKR 1,750", "Completed"),
                new Transaction("TXN-006", "2024-01-14 16:30", "Ali", "PKR 920", "Completed"),
                new Transaction("TXN-007", "2024-01-14 15:45", "Hareem", "PKR 1,380", "Completed")
        );
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            transactionsTable.setItems(transactions);
        } else {
            ObservableList<Transaction> filtered = FXCollections.observableArrayList();
            for (Transaction transaction : transactions) {
                if (transaction.getTransactionId().toLowerCase().contains(searchText) ||
                        transaction.getCashier().toLowerCase().contains(searchText) ||
                        transaction.getAmount().toLowerCase().contains(searchText)) {
                    filtered.add(transaction);
                }
            }
            transactionsTable.setItems(filtered);
        }
    }

    @FXML
    private void showTransactionDetails() {
        Transaction selected = transactionsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Transaction Details");
            alert.setHeaderText("Details for " + selected.getTransactionId());
            alert.setContentText("Cashier: " + selected.getCashier() +
                    "\nAmount: " + selected.getAmount() +
                    "\nStatus: " + selected.getStatus() +
                    "\nDate: " + selected.getDateTime());
            alert.showAndWait();
        }
    }

    // Navigation methods
    @FXML
    private void showDashboard() {
        loadScene("/com/pos/view/dashboard.fxml");
    }

    @FXML
    private void showTransactionHome() {
        loadScene("/com/pos/view/transactions-home.fxml");
    }

    @FXML
    private void showCreateTransaction() {
        loadScene("/com/pos/view/transactions-create.fxml");
    }

    @FXML
    private void showProducts() {
        System.out.println("Navigate to Products");
    }

    @FXML
    private void showCustomers() {
        System.out.println("Navigate to Customers");
    }

    @FXML
    private void showSettings() {
        //System.out.println("Navigate to Settings");
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
        System.out.println("Logout clicked");
    }

    private void loadScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Transaction data model
    public static class Transaction {
        private final String transactionId;
        private final String dateTime;
        private final String cashier;
        private final String amount;
        private final String status;

        public Transaction(String transactionId, String dateTime, String cashier, String amount, String status) {
            this.transactionId = transactionId;
            this.dateTime = dateTime;
            this.cashier = cashier;
            this.amount = amount;
            this.status = status;
        }

        public String getTransactionId() { return transactionId; }
        public String getDateTime() { return dateTime; }
        public String getCashier() { return cashier; }
        public String getAmount() { return amount; }
        public String getStatus() { return status; }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}