package com.pos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML private Label currentTimeLabel;
    @FXML private Label currentDateLabel;
    @FXML private Label welcomeLabel;

    // Sales Reports Section
    @FXML private DatePicker salesStartDate;
    @FXML private DatePicker salesEndDate;
    @FXML private ToggleGroup reportTypeGroup;
    @FXML private Button generateSalesReport;
    @FXML private Label totalSalesLabel;
    @FXML private Label totalOrdersLabel;
    @FXML private Label avgOrderValueLabel;
    @FXML private Label peakHourLabel;
    @FXML private LineChart<String, Number> salesChart;
    @FXML private TableView<?> salesTable;

    // Cashier Reports Section
    @FXML private ComboBox<String> cashierFilter;
    @FXML private DatePicker cashierStartDate;
    @FXML private DatePicker cashierEndDate;
    @FXML private Button generateCashierReport;
    @FXML private Label cashierSalesLabel;
    @FXML private Label cashierOrdersLabel;
    @FXML private Label cashierAvgValueLabel;
    @FXML private BarChart<String, Number> cashierChart;
    @FXML private TableView<?> cashierTable;

    // Tax Reports Section
    @FXML private Label grossRevenueLabel;
    @FXML private Label netRevenueLabel;
    @FXML private Label taxCollectedLabel;
    @FXML private Label discountsLabel;
    @FXML private PieChart taxPieChart;

    // Product Performance Section
    @FXML private ListView<String> topProductsList;
    @FXML private ListView<String> slowProductsList;
    @FXML private BarChart<String, Number> productChart;
    @FXML private TableView<?> productTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupClock();
        setupWelcomeMessage();
        initializeReports();
        loadSampleData();
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

    private void initializeReports() {
        // Initialize cashier dropdown
        cashierFilter.getItems().addAll("All Cashiers", "Hareem", "Ali", "Sara", "Usman");
        cashierFilter.setValue("All Cashiers");

        // Set default dates to current week
        java.time.LocalDate today = java.time.LocalDate.now();
        salesStartDate.setValue(today.minusDays(7));
        salesEndDate.setValue(today);
        cashierStartDate.setValue(today.minusDays(7));
        cashierEndDate.setValue(today);
    }

    private void loadSampleData() {
        loadSalesChartData();
        loadCashierChartData();
        loadTaxPieChartData();
        loadProductChartData();
        loadTopProductsList();
        loadSlowProductsList();
    }

    private void loadSalesChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Sales");

        series.getData().add(new XYChart.Data<>("Mon", 12500));
        series.getData().add(new XYChart.Data<>("Tue", 18300));
        series.getData().add(new XYChart.Data<>("Wed", 14200));
        series.getData().add(new XYChart.Data<>("Thu", 19600));
        series.getData().add(new XYChart.Data<>("Fri", 23400));
        series.getData().add(new XYChart.Data<>("Sat", 28700));
        series.getData().add(new XYChart.Data<>("Sun", 15800));

        salesChart.getData().add(series);
    }

    private void loadCashierChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales by Cashier");

        series.getData().add(new XYChart.Data<>("Hareem", 45280));
        series.getData().add(new XYChart.Data<>("Ali", 38150));
        series.getData().add(new XYChart.Data<>("Sara", 29500));
        series.getData().add(new XYChart.Data<>("Usman", 42100));

        cashierChart.getData().add(series);
    }

    private void loadTaxPieChartData() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("GST", 8240),
                new PieChart.Data("Service Charge", 2850),
                new PieChart.Data("VAT", 1290)
        );
        taxPieChart.setData(pieChartData);
    }

    private void loadProductChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Products Sold");

        series.getData().add(new XYChart.Data<>("Coca Cola", 145));
        series.getData().add(new XYChart.Data<>("Lays Chips", 98));
        series.getData().add(new XYChart.Data<>("Mineral Water", 167));
        series.getData().add(new XYChart.Data<>("Chocolate Bar", 76));
        series.getData().add(new XYChart.Data<>("Fresh Milk", 54));

        productChart.getData().add(series);
    }

    private void loadTopProductsList() {
        ObservableList<String> topProducts = FXCollections.observableArrayList(
                "Coca Cola 330ml - 145 units",
                "Mineral Water 500ml - 167 units",
                "Lays Classic Chips - 98 units",
                "Chocolate Bar - 76 units",
                "Fresh Milk 1L - 54 units",
                "White Bread - 43 units",
                "Apple Juice 1L - 38 units",
                "Cheddar Cheese 200g - 32 units",
                "Potato Chips - 29 units",
                "Yogurt 150g - 27 units"
        );
        topProductsList.setItems(topProducts);
    }

    private void loadSlowProductsList() {
        ObservableList<String> slowProducts = FXCollections.observableArrayList(
                "USB Cable - 3 units",
                "Notebook - 5 units",
                "Pen Set - 7 units",
                "Croissant - 8 units",
                "Orange Juice 500ml - 9 units"
        );
        slowProductsList.setItems(slowProducts);
    }

    @FXML
    private void generateSalesReport() {
        // In a real application, this would fetch data from database
        // For now, we'll just show a success message
        showAlert("Success", "Sales report generated for the selected period.");

        // Update metrics with new data
        totalSalesLabel.setText("PKR 89,150");
        totalOrdersLabel.setText("152");
        avgOrderValueLabel.setText("PKR 587");
        peakHourLabel.setText("3 PM – 4 PM");
    }

    @FXML
    private void generateCashierReport() {
        // In a real application, this would fetch data from database
        showAlert("Success", "Cashier report generated for the selected period.");

        // Update metrics with new data
        cashierSalesLabel.setText("PKR 48,750");
        cashierOrdersLabel.setText("84");
        cashierAvgValueLabel.setText("PKR 580");
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
        // Already on reports page
    }

    @FXML
    private void showSettings() {
        loadScene("/com/pos/view/settings.fxml");
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
//                    // Load login screen - KEEP NORMAL SIZE FOR LOGIN
//                    Parent root = FXMLLoader.load(getClass().getResource("/com/pos/view/login.fxml"));
//                    Stage stage = (Stage) welcomeLabel.getScene().getWindow();
//                    Scene scene = new Scene(root, 1000, 700);
//                    scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
//                    stage.setTitle("NexusPOS - Login");
//                    stage.setScene(scene);
//                    stage.setResizable(false);
//                    stage.centerOnScreen();
//                    stage.show();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void loadScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());

            // SET FULLSCREEN FOR ALL PAGES
            stage.setMaximized(true);
            stage.setScene(scene);
        } catch (Exception e) {
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