package com.pos.controller;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.io.IOException;

public class CustomersController implements Initializable {

    @FXML private Label currentTimeLabel;
    @FXML private Label currentDateLabel;
    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchTypeCombo;
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, String> colCustomerId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, Integer> colLoyaltyPoints;
    @FXML private TableColumn<Customer, String> colTotalPurchases;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    // Customer Details Panel
    @FXML private Label detailName;
    @FXML private Label detailPhone;
    @FXML private Label detailEmail;
    @FXML private Label detailLoyaltyPoints;
    @FXML private Label detailNotes;
    @FXML private TableView<Purchase> purchaseHistoryTable;
    @FXML private TableColumn<Purchase, String> colPurchaseDate;
    @FXML private TableColumn<Purchase, String> colTransactionId;
    @FXML private TableColumn<Purchase, String> colAmount;
    @FXML private TableColumn<Purchase, String> colItems;
    @FXML private TableColumn<Purchase, String> colRefund;

    private ObservableList<Customer> customers = FXCollections.observableArrayList();
    private ObservableList<Customer> filteredCustomers = FXCollections.observableArrayList();
    private ObservableList<Purchase> purchaseHistory = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupClock();
        setupWelcomeMessage();
        setupSearchFilters();
        setupCustomersTable();
        setupPurchaseHistoryTable();
        loadDummyData();
        setupTableSelectionListener();
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

    private void setupSearchFilters() {
        // Search type filter
        searchTypeCombo.getItems().addAll("Name", "Phone", "Email", "Customer ID");
        searchTypeCombo.setValue("Name");

        // Add listeners for real-time filtering
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterCustomers());
        searchTypeCombo.valueProperty().addListener((observable, oldValue, newValue) -> filterCustomers());
    }

    private void setupCustomersTable() {
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colLoyaltyPoints.setCellValueFactory(new PropertyValueFactory<>("loyaltyPoints"));
        colTotalPurchases.setCellValueFactory(new PropertyValueFactory<>("totalPurchases"));

        customersTable.setItems(filteredCustomers);

        // Custom cell factory for loyalty points highlighting
        colLoyaltyPoints.setCellFactory(column -> new TableCell<Customer, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    if (item > 1000) {
                        setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void setupPurchaseHistoryTable() {
        colPurchaseDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTransactionId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colItems.setCellValueFactory(new PropertyValueFactory<>("items"));
        colRefund.setCellValueFactory(new PropertyValueFactory<>("refund"));

        purchaseHistoryTable.setItems(purchaseHistory);
    }

    private void setupTableSelectionListener() {
        customersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    boolean itemSelected = newValue != null;
                    editButton.setDisable(!itemSelected);
                    deleteButton.setDisable(!itemSelected);

                    if (newValue != null) {
                        updateCustomerDetails(newValue);
                        loadPurchaseHistory(newValue);
                    } else {
                        clearCustomerDetails();
                    }
                }
        );
    }

    private void updateCustomerDetails(Customer customer) {
        detailName.setText(customer.getName());
        detailPhone.setText(customer.getPhone());
        detailEmail.setText(customer.getEmail());
        detailLoyaltyPoints.setText(String.valueOf(customer.getLoyaltyPoints()));
        detailNotes.setText(customer.getNotes());
    }

    private void clearCustomerDetails() {
        detailName.setText("");
        detailPhone.setText("");
        detailEmail.setText("");
        detailLoyaltyPoints.setText("");
        detailNotes.setText("");
        purchaseHistory.clear();
    }

    private void loadPurchaseHistory(Customer customer) {
        purchaseHistory.clear();
        // Load dummy purchase history for the selected customer
        purchaseHistory.addAll(
                new Purchase("2024-01-15 14:30", "TXN-001", "PKR 1,250", "5 items", "No"),
                new Purchase("2024-01-10 11:20", "TXN-045", "PKR 850", "3 items", "No"),
                new Purchase("2024-01-05 16:45", "TXN-032", "PKR 2,100", "8 items", "Yes")
        );
    }

    private void loadDummyData() {
        customers.addAll(
                new Customer("C-001", "Ali Khan", "0300-1234567", "ali.khan@email.com", 1250, "PKR 12,500", "Regular customer, prefers cash payments"),
                new Customer("C-002", "Sara Ahmed", "0312-7654321", "sara.ahmed@email.com", 850, "PKR 8,200", "Loyalty member since 2023"),
                new Customer("C-003", "Usman Malik", "0333-9876543", "usman.malik@email.com", 450, "PKR 4,500", "New customer"),
                new Customer("C-004", "Fatima Noor", "0345-1122334", "fatima.noor@email.com", 2100, "PKR 21,000", "VIP customer, high spending"),
                new Customer("C-005", "Bilal Hassan", "0301-5566778", "bilal.hassan@email.com", 320, "PKR 3,200", "Prefers card payments"),
                new Customer("C-006", "Ayesha Raza", "0321-9988776", "", 680, "PKR 6,800", "No email provided"),
                new Customer("C-007", "Omar Farooq", "0334-4433221", "omar.farooq@email.com", 1500, "PKR 15,000", "Frequent buyer"),
                new Customer("C-008", "Zainab Kareem", "0305-6677889", "zainab.kareem@email.com", 950, "PKR 9,500", "Likes discounts")
        );
        filteredCustomers.setAll(customers);
    }

    private void filterCustomers() {
        String searchText = searchField.getText().toLowerCase();
        String searchType = searchTypeCombo.getValue();

        if (searchText.isEmpty()) {
            filteredCustomers.setAll(customers);
            return;
        }

        ObservableList<Customer> filtered = FXCollections.observableArrayList();

        for (Customer customer : customers) {
            boolean matches = false;

            switch (searchType) {
                case "Name":
                    matches = customer.getName().toLowerCase().contains(searchText);
                    break;
                case "Phone":
                    matches = customer.getPhone().replace("-", "").contains(searchText.replace("-", ""));
                    break;
                case "Email":
                    matches = customer.getEmail().toLowerCase().contains(searchText);
                    break;
                case "Customer ID":
                    matches = customer.getCustomerId().toLowerCase().contains(searchText);
                    break;
            }

            if (matches) {
                filtered.add(customer);
            }
        }

        filteredCustomers.setAll(filtered);
    }

    @FXML
    private void handleSearch() {
        filterCustomers();
    }

    @FXML
    private void handleRefresh() {
        filterCustomers();
        showAlert("Refreshed", "Customer list has been refreshed.");
    }

    @FXML
    private void handleAddCustomer() {
        showCustomerDialog(null);
    }

    @FXML
    private void handleEditCustomer() {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            showCustomerDialog(selectedCustomer);
        }
    }

    @FXML
    private void handleDeleteCustomer() {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Customer");
            alert.setHeaderText("Delete " + selectedCustomer.getName() + "?");
            alert.setContentText("Are you sure you want to delete this customer? This action cannot be undone.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    customers.remove(selectedCustomer);
                    filterCustomers();
                    clearCustomerDetails();
                    showAlert("Success", "Customer deleted successfully!");
                }
            });
        }
    }

    @FXML
    private void handleViewReceipt() {
        Purchase selectedPurchase = purchaseHistoryTable.getSelectionModel().getSelectedItem();
        if (selectedPurchase != null) {
            showAlert("Receipt View", "Receipt for " + selectedPurchase.getTransactionId() +
                    "\nAmount: " + selectedPurchase.getAmount() +
                    "\nDate: " + selectedPurchase.getDate());
        } else {
            showAlert("Selection Required", "Please select a purchase to view receipt.");
        }
    }

    @FXML
    private void handleIssueRefund() {
        Purchase selectedPurchase = purchaseHistoryTable.getSelectionModel().getSelectedItem();
        if (selectedPurchase != null) {
            if (selectedPurchase.getRefund().equals("Yes")) {
                showAlert("Already Refunded", "This transaction has already been refunded.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Issue Refund");
            alert.setHeaderText("Refund Transaction " + selectedPurchase.getTransactionId());
            alert.setContentText("Amount: " + selectedPurchase.getAmount() +
                    "\n\nAre you sure you want to process this refund?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // In real app, this would update the database
                    selectedPurchase.setRefund("Yes");
                    purchaseHistoryTable.refresh();
                    showAlert("Refund Processed", "Refund has been processed successfully.");
                }
            });
        } else {
            showAlert("Selection Required", "Please select a purchase to issue refund.");
        }
    }

    private void showCustomerDialog(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pos/view/customer-dialog.fxml"));
            Parent root = loader.load();

            CustomerDialogController dialogController = loader.getController();
            dialogController.setCustomersController(this);
            if (customer != null) {
                dialogController.setCustomerForEditing(customer);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(customer == null ? "Add New Customer" : "Edit Customer");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(welcomeLabel.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open customer dialog: " + e.getMessage());
        }
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        filterCustomers();
        showAlert("Success", "Customer added successfully!");
    }

    public void updateCustomer(Customer oldCustomer, Customer updatedCustomer) {
        int index = customers.indexOf(oldCustomer);
        if (index != -1) {
            customers.set(index, updatedCustomer);
            filterCustomers();
            // Update details if this customer is currently selected
            if (customersTable.getSelectionModel().getSelectedItem() == oldCustomer) {
                updateCustomerDetails(updatedCustomer);
            }
            showAlert("Success", "Customer updated successfully!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods - UPDATED FOR FULLSCREEN
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
        // Already on customers page
    }

    @FXML
    private void showSettings() {
        loadScene("/com/pos/view/settings.fxml");
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
                    try {
                        // Load login screen - KEEP NORMAL SIZE FOR LOGIN
                        Parent root = FXMLLoader.load(getClass().getResource("/com/pos/view/login.fxml"));
                        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                        Scene scene = new Scene(root, 1000, 700);

                        // Load CSS if available
                        try {
                            scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
                        } catch (Exception cssEx) {
                            System.out.println("CSS not loaded: " + cssEx.getMessage());
                        }

                        stage.setTitle("NexusPOS - Login");
                        stage.setScene(scene);
                        stage.setResizable(false);
                        stage.centerOnScreen();
                        stage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        // Show error message to user
                        Platform.runLater(() -> showAlert("Error", "Failed to load login screen: " + e.getMessage()));
                    }
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

    // Customer data model
    public static class Customer {
        private final String customerId;
        private final String name;
        private final String phone;
        private final String email;
        private final int loyaltyPoints;
        private final String totalPurchases;
        private final String notes;

        public Customer(String customerId, String name, String phone, String email,
                        int loyaltyPoints, String totalPurchases, String notes) {
            this.customerId = customerId;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.loyaltyPoints = loyaltyPoints;
            this.totalPurchases = totalPurchases;
            this.notes = notes;
        }

        public String getCustomerId() { return customerId; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public int getLoyaltyPoints() { return loyaltyPoints; }
        public String getTotalPurchases() { return totalPurchases; }
        public String getNotes() { return notes; }
    }

    // Purchase history data model
    public static class Purchase {
        private final String date;
        private final String transactionId;
        private final String amount;
        private final String items;
        private String refund;

        public Purchase(String date, String transactionId, String amount, String items, String refund) {
            this.date = date;
            this.transactionId = transactionId;
            this.amount = amount;
            this.items = items;
            this.refund = refund;
        }

        public String getDate() { return date; }
        public String getTransactionId() { return transactionId; }
        public String getAmount() { return amount; }
        public String getItems() { return items; }
        public String getRefund() { return refund; }
        public void setRefund(String refund) { this.refund = refund; }
    }
}