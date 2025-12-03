package com.pos.controller;

import com.pos.database.DatabaseManager;
import com.pos.database.managers.SalesTransactionDBManager;
import com.pos.models.SalesTransaction;
import com.pos.models.UserSession;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


// Give the top cards actual values. And since we are not handling categories, I have to replace that with something else.
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
    @FXML private Label userInfo;

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private SalesTransactionDBManager transactionManager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupClock();
        setupWelcomeMessage();
        setupDashboardCards();
        setupRecentTransactionsTable();

        transactionManager = DatabaseManager.getInstance().getSalesTransactionManager();
        loadDummyData();
        loadTransactionsFromDB();
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
        userInfo.setText("Logged in as " + UserSession.getInstance().getFullName() + " (" + UserSession.getInstance().getRole().toLowerCase() + ")");
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

        recentTransactionsTable.setItems(transactions);
    }


    private void loadTransactionsFromDB() {
        try {
            List<SalesTransaction> salesTransactions = transactionManager.getSalesTransactionsOfToday();
            transactions.clear(); // CLear the table (Because when we are refreshing, we call the initialize function again so we need this)
            BigDecimal todaysSalesProfit =  BigDecimal.ZERO;
            Integer todaysSalesCount = salesTransactions.size();
            for (SalesTransaction salesTransaction : salesTransactions) {
                Transaction displayTransaction = convertToDisplayTransaction(salesTransaction);
                transactions.add(displayTransaction);
                todaysSalesProfit = todaysSalesProfit.add(salesTransaction.getFinalAmount());
            }
            recentTransactionsTable.refresh();

            todaySalesLabel.setText("PKR " + todaysSalesProfit);
            transactionsCountLabel.setText(todaysSalesCount + " Orders");

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

        // Get cashier name (Using the current user's full name here) ((THat iniital idea was wrong..)
        String cashier = getCashierName(salesTransaction.getCashierID());

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String amount = "PKR " + df.format(salesTransaction.getFinalAmount().doubleValue());

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


    // Since I already have transactions
    private void loadDummyData() {
        topCategoryLabel.setText("TBA");
        lowStockLabel.setText("TBA");
    }

    // Navigation methods
    @FXML
    private void showDashboard() {
        // Already on dashboard - maybe refresh data? And we can then refresh every page? As Transactions history can also be refreshed.
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
    @FXML
    private void logout() {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/pos/view/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene currentScene = stage.getScene();
            currentScene.setRoot(loginRoot);
            // If login has different CSS, add it here
            currentScene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
            stage.setMaximized(false); // Login shouldn't be maximized
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Logout Error", "Failed to logout: " + e.getMessage());
        }
    }

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

//    @FXML
//    private void logout() {
//        try {
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//            alert.setTitle("Logout");
//            alert.setHeaderText("Confirm Logout");
//            alert.setContentText("Are you sure you want to logout?");
//
//            alert.showAndWait().ifPresent(response -> {
//                if (response == ButtonType.OK) {
//                    try {
//                        Parent root = FXMLLoader.load(getClass().getResource("/com/pos/view/login.fxml"));
//                        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
//                        Scene scene = new Scene(root, 1000, 700);
//                        scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
//                        stage.setTitle("NexusPOS - Login");
//                        stage.setScene(scene);
//                        stage.setResizable(false);
//                        stage.show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


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