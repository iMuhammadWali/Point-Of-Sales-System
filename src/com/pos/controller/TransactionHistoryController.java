package com.pos.controller;

import com.pos.models.UserSession;
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
import java.util.*;
import com.pos.database.managers.*;
import com.pos.database.*;
import com.pos.database.DatabaseManager;
import com.pos.database.managers.SalesTransactionDBManager;
import com.pos.models.SalesTransaction;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.pos.models.*;

// TODO: fix this..
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

    private SalesTransactionDBManager transactionManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupClock();
        setupWelcomeMessage();
        setupTransactionsTable();

        transactionManager = DatabaseManager.getInstance().getSalesTransactionManager();

        loadTransactionsFromDB();
    }

    // The same methods written in every file by my group partner.
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


    private void setupTransactionsTable() {
        colTransactionId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colDateTime.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        colCashier.setCellValueFactory(new PropertyValueFactory<>("cashier"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        transactionsTable.setItems(transactions);
    }

    private void loadTransactionsFromDB() {
        try {
            List<SalesTransaction> salesTransactions = transactionManager.getAllSalesTransactions();

            transactions.clear();

            for (SalesTransaction salesTransaction : salesTransactions) {
                Transaction displayTransaction = convertToDisplayTransaction(salesTransaction);
                transactions.add(displayTransaction);
            }

            // Refresh table
            transactionsTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load transactions: " + e.getMessage());
        }
    }

    private Transaction convertToDisplayTransaction(SalesTransaction salesTransaction) {

        // I have no clue but this is some real world formatting that AI did.
        String transactionId = "TXN-" + String.format("%05d", salesTransaction.getSaleID());

        // Format date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTime = dateFormat.format(
                java.sql.Timestamp.valueOf(salesTransaction.getCreatedAt())
        );

        // Get cashier name (Using the current user's full name here)
        String cashier = getCashierName(salesTransaction.getCashierID());

        // Format amount
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String amount = "PKR " + df.format(salesTransaction.getFinalAmount().doubleValue());

        // Get status
        String status = salesTransaction.getStatus();

        return new Transaction(transactionId, dateTime, cashier, amount, status);
    }

    private String getCashierName(int cashierId) {
        try {
            // USe the Database manager.
            return DatabaseManager.getInstance().getUserManager().getUserDetailsFromID(cashierId).getFullName();

        } catch (Exception e) {
            return "Cashier #" + cashierId;
        }
    }

    @FXML
    private void showTransactionDetails() {
        Transaction selected = transactionsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                // Extract transaction ID from display format (remove "TXN-" prefix)
                int saleId = Integer.parseInt(selected.getTransactionId().replace("TXN-", ""));

                // Get full transaction details from database
                SalesTransaction fullTransaction = transactionManager.getSalesTransactionById(saleId);

                if (fullTransaction != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Transaction Details");
                    alert.setHeaderText("Details for " + selected.getTransactionId());

                    // Build detailed content
                    StringBuilder content = new StringBuilder();
                    content.append("Transaction ID: ").append(selected.getTransactionId()).append("\n");
                    content.append("Date: ").append(selected.getDateTime()).append("\n");
                    content.append("Cashier: ").append(selected.getCashier()).append("\n");
                    content.append("Amount: ").append(selected.getAmount()).append("\n");
                    content.append("Status: ").append(selected.getStatus()).append("\n");
                    content.append("Subtotal: PKR ").append(fullTransaction.getTotalAmount()).append("\n");
                    content.append("Tax: PKR ").append(fullTransaction.getTaxAmount()).append("\n");
                    content.append("Discount: PKR ").append(fullTransaction.getDiscountAmount()).append("\n");

                    alert.setContentText(content.toString());
                    alert.showAndWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load transaction details: " + e.getMessage());
            }
        }
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


    // Navigation methods
    private void navigateTo(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();

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
    @FXML
    private void showTransactionHome() {
        navigateTo("/com/pos/view/transactions-home.fxml");
    }
    @FXML
    private void showCreateTransaction() {
        navigateTo("/com/pos/view/transactions-create.fxml");
    }


//    @FXML
//    private void showReports() {
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("/com/pos/view/reports.fxml"));
//            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
//            Scene scene = new Scene(root);
//            scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
//            stage.setMaximized(true);
//            stage.setScene(scene);
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert("Navigation Error", "Failed to load Reports page: " + e.getMessage());
//        }
//    }

    @FXML
    private void logout() {
        System.out.println("Logout clicked");
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