package com.pos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.application.Platform;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class CreateTransactionController implements Initializable {

    @FXML private Label currentTimeLabel;
    @FXML private Label currentDateLabel;
    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colProductName;
    @FXML private TableColumn<CartItem, Double> colPrice;
    @FXML private TableColumn<CartItem, Integer> colQuantity;
    @FXML private TableColumn<CartItem, Double> colSubtotal;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel; // This was missing in FXML
    @FXML private TextField discountField;
    @FXML private TextField loyaltyPointsField;
    @FXML private Label discountAmountLabel;
    @FXML private Label loyaltyDiscountLabel;
    @FXML private Label finalTotalLabel;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private double discountAmount = 0.0;
    private double loyaltyDiscount = 0.0;
    private boolean isFXMLInitialized = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupClock();
        setupWelcomeMessage();
        setupCartTable();

        // Initialize discount fields first
        if (discountField != null && loyaltyPointsField != null) {
            setupDiscountListeners();
        }

        // Mark as initialized and update totals
        isFXMLInitialized = true;
        updateTotals();
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
        Platform.runLater(() -> {
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

    private void setupCartTable() {
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        cartTable.setItems(cartItems);

    }

    private void setupDiscountListeners() {
        discountField.textProperty().addListener((observable, oldValue, newValue) -> applyDiscount());
        loyaltyPointsField.textProperty().addListener((observable, oldValue, newValue) -> applyLoyaltyPoints());
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (!searchText.isEmpty()) {
            if (searchText.equals("coca") || searchText.equals("12345")) {
                addProductToCart("Coca Cola 330ml", 1.50, "P-001");
            } else if (searchText.equals("lays") || searchText.equals("12346")) {
                addProductToCart("Lays Classic Chips", 2.00, "P-002");
            } else if (searchText.equals("water") || searchText.equals("12347")) {
                addProductToCart("Mineral Water 500ml", 0.75, "P-003");
            } else if (searchText.equals("chocolate") || searchText.equals("12348")) {
                addProductToCart("Chocolate Bar", 1.25, "P-004");
            } else {
                showAlert("Product Not Found", "No product found for: " + searchText);
            }
            searchField.clear();
        }
    }

    @FXML
    private void addSampleProduct() {
        String[] products = {"Coca Cola 330ml", "Lays Classic Chips", "Mineral Water 500ml", "Chocolate Bar"};
        double[] prices = {1.50, 2.00, 0.75, 1.25};
        String[] codes = {"P-001", "P-002", "P-003", "P-004"};
        int randomIndex = (int) (Math.random() * products.length);
        addProductToCart(products[randomIndex], prices[randomIndex], codes[randomIndex]);
    }

    private void addProductToCart(String productName, double price, String productCode) {
        for (CartItem item : cartItems) {
            if (item.getProductName().equals(productName)) {
                item.setQuantity(item.getQuantity() + 1);
                cartTable.refresh();
                updateTotals();
                return;
            }
        }

        cartItems.add(new CartItem(productName, price, 1, productCode));
        updateTotals();
    }

    @FXML
    private void removeFromCart() {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (selectedItem.getQuantity() > 1) {
                selectedItem.setQuantity(selectedItem.getQuantity() - 1);
                cartTable.refresh();
            } else {
                cartItems.remove(selectedItem);
            }
            updateTotals();
        }
    }

    @FXML
    private void clearCart() {
        cartItems.clear();
        discountField.clear();
        loyaltyPointsField.clear();
        discountAmount = 0.0;
        loyaltyDiscount = 0.0;
        updateTotals();
    }

    @FXML
    private void completeTransaction() {
        if (cartItems.isEmpty()) {
            showAlert("Empty Cart", "Please add items to cart before completing transaction.");
            return;
        }

        showPaymentOptions();
    }

    private void showPaymentOptions() {
        double finalTotal = calculateFinalTotal();

        Dialog<PaymentResult> dialog = new Dialog<>();
        dialog.setTitle("Complete Payment");
        dialog.setHeaderText("Total Amount: PKR " + String.format("%.2f", finalTotal));

        ButtonType cashButtonType = new ButtonType("Cash Payment", ButtonBar.ButtonData.OK_DONE);
        ButtonType cardButtonType = new ButtonType("Card Payment", ButtonBar.ButtonData.OK_DONE);
        ButtonType splitButtonType = new ButtonType("Split Payment", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(cashButtonType, cardButtonType, splitButtonType, cancelButtonType);

        VBox paymentContent = new VBox(10);
        paymentContent.setPadding(new Insets(20));

        Label amountLabel = new Label("Amount Due: PKR " + String.format("%.2f", finalTotal));
        amountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        paymentContent.getChildren().add(amountLabel);
        dialog.getDialogPane().setContent(paymentContent);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == cashButtonType) {
                return new PaymentResult("CASH", finalTotal);
            } else if (dialogButton == cardButtonType) {
                return new PaymentResult("CARD", finalTotal);
            } else if (dialogButton == splitButtonType) {
                return new PaymentResult("SPLIT", finalTotal);
            }
            return null;
        });

        Optional<PaymentResult> result = dialog.showAndWait();
        result.ifPresent(this::processPayment);
    }

    private void processPayment(PaymentResult paymentResult) {
        if (paymentResult.getPaymentType().equals("SPLIT")) {
            showSplitPaymentDialog();
        } else {
            showPaymentAmountDialog(paymentResult.getPaymentType(), paymentResult.getAmount());
        }
    }

    private void showSplitPaymentDialog() {
        double finalTotal = calculateFinalTotal();

        Dialog<SplitPaymentResult> dialog = new Dialog<>();
        dialog.setTitle("Split Payment");
        dialog.setHeaderText("Split payment for PKR " + String.format("%.2f", finalTotal));

        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);

        VBox splitContent = new VBox(10);
        splitContent.setPadding(new Insets(20));

        TextField cashAmountField = new TextField();
        cashAmountField.setPromptText("Cash Amount");
        TextField cardAmountField = new TextField();
        cardAmountField.setPromptText("Card Amount");

        splitContent.getChildren().addAll(
                new Label("Enter split amounts:"),
                new Label("Cash:"), cashAmountField,
                new Label("Card:"), cardAmountField
        );

        dialog.getDialogPane().setContent(splitContent);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                try {
                    double cashAmount = Double.parseDouble(cashAmountField.getText());
                    double cardAmount = Double.parseDouble(cardAmountField.getText());
                    double totalPaid = cashAmount + cardAmount;

                    if (Math.abs(totalPaid - finalTotal) < 0.01) {
                        return new SplitPaymentResult(cashAmount, cardAmount);
                    } else {
                        showAlert("Invalid Amount", "Split amounts must equal the total: PKR " + finalTotal);
                    }
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for split amounts.");
                }
            }
            return null;
        });

        Optional<SplitPaymentResult> result = dialog.showAndWait();
        result.ifPresent(this::completeSplitPayment);
    }

    private void showPaymentAmountDialog(String paymentType, double amountDue) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(paymentType + " Payment");
        dialog.setHeaderText("Total Amount: PKR " + String.format("%.2f", amountDue));
        dialog.setContentText("Enter amount received:");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double received = Double.parseDouble(amountStr);
                double change = received - amountDue;

                if (change < 0) {
                    showAlert("Insufficient Amount", "Amount received is less than total.");
                    return;
                }

                showSuccessDialog(change, paymentType);
            } catch (NumberFormatException e) {
                showAlert("Invalid Amount", "Please enter a valid number.");
            }
        });
    }

    private void completeSplitPayment(SplitPaymentResult splitResult) {
        double change = (splitResult.getCashAmount() + splitResult.getCardAmount()) - calculateFinalTotal();

        if (change < 0) {
            showAlert("Insufficient Amount", "Total payment is less than amount due.");
            return;
        }

        showSuccessDialog(change, "SPLIT");
    }

    private void showSuccessDialog(double change, String paymentType) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaction Complete");
        alert.setHeaderText("Sale Completed Successfully!");

        String paymentInfo = paymentType.equals("SPLIT") ?
                "\nPayment Method: Split Payment" : "\nPayment Method: " + paymentType;

        alert.setContentText("Change: PKR " + String.format("%.2f", change) +
                paymentInfo + "\n\nReceipt has been generated.");

        ButtonType printButton = new ButtonType("Print Receipt");
        ButtonType emailButton = new ButtonType("Email Receipt");
        ButtonType doneButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(printButton, emailButton, doneButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == printButton) {
                printReceipt();
            } else if (buttonType == emailButton) {
                emailReceipt();
            }
            cartItems.clear();
            discountField.clear();
            loyaltyPointsField.clear();
            discountAmount = 0.0;
            loyaltyDiscount = 0.0;
            updateTotals();
        });
    }

    private void applyDiscount() {
        try {
            String discountText = discountField.getText().trim();
            if (!discountText.isEmpty()) {
                if (discountText.endsWith("%")) {
                    double percentage = Double.parseDouble(discountText.replace("%", ""));
                    discountAmount = (calculateSubtotal() * percentage) / 100;
                } else {
                    discountAmount = Double.parseDouble(discountText);
                }
            } else {
                discountAmount = 0.0;
            }
            updateTotals();
        } catch (NumberFormatException e) {
            showAlert("Invalid Discount", "Please enter a valid discount amount or percentage.");
            discountField.clear();
        }
    }

    private void applyLoyaltyPoints() {
        try {
            String pointsText = loyaltyPointsField.getText().trim();
            if (!pointsText.isEmpty()) {
                int points = Integer.parseInt(pointsText);
                loyaltyDiscount = points * 0.10;
            } else {
                loyaltyDiscount = 0.0;
            }
            updateTotals();
        } catch (NumberFormatException e) {
            showAlert("Invalid Points", "Please enter a valid number of loyalty points.");
            loyaltyPointsField.clear();
        }
    }

    private void updateTotals() {
        if (!isFXMLInitialized) return;

        double subtotal = calculateSubtotal();
        double tax = subtotal * 0.16;
        double total = subtotal + tax;
        double finalTotal = total - discountAmount - loyaltyDiscount;

        // Safe updates with null checks for ALL labels
        if (subtotalLabel != null) {
            subtotalLabel.setText(String.format("PKR %.2f", subtotal));
        }
        if (taxLabel != null) {
            taxLabel.setText(String.format("PKR %.2f", tax));
        }
        if (totalLabel != null) {
            totalLabel.setText(String.format("PKR %.2f", total));
        }
        if (discountAmountLabel != null) {
            discountAmountLabel.setText(String.format("-PKR %.2f", discountAmount));
        }
        if (loyaltyDiscountLabel != null) {
            loyaltyDiscountLabel.setText(String.format("-PKR %.2f", loyaltyDiscount));
        }
        if (finalTotalLabel != null) {
            finalTotalLabel.setText(String.format("PKR %.2f", finalTotal));
        }
    }

    private double calculateSubtotal() {
        return cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    private double calculateFinalTotal() {
        double subtotal = calculateSubtotal();
        double tax = subtotal * 0.16;
        return (subtotal + tax) - discountAmount - loyaltyDiscount;
    }

    private void printReceipt() {
        System.out.println("=== NEXUS POS RECEIPT ===");
        System.out.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println("Cashier: Hareem");
        System.out.println("------------------------");
        for (CartItem item : cartItems) {
            System.out.printf("%s x%d = PKR %.2f%n", item.getProductName(), item.getQuantity(), item.getSubtotal());
        }
        System.out.println("------------------------");
        System.out.printf("Subtotal: PKR %.2f%n", calculateSubtotal());
        System.out.printf("Tax (16%%): PKR %.2f%n", calculateSubtotal() * 0.16);
        System.out.printf("Discount: -PKR %.2f%n", discountAmount);
        System.out.printf("Loyalty Discount: -PKR %.2f%n", loyaltyDiscount);
        System.out.printf("TOTAL: PKR %.2f%n", calculateFinalTotal());
        System.out.println("Thank you for your business!");
    }

    private void emailReceipt() {
        showAlert("Email Receipt", "Receipt has been sent to customer's email.");
    }

    @FXML
    private void handleRefund() {
        showAlert("Refund Feature", "Refund functionality will be implemented in the next update.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
    private void showReports() {
        loadScene("/com/pos/view/reports.fxml");
    }

    @FXML
    private void showSettings() {
        loadScene("/com/pos/view/settings.fxml");
    }

    @FXML
    private void logout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText("Confirm Logout");
            alert.setContentText("Are you sure you want to logout?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Parent root = FXMLLoader.load(getClass().getResource("/com/pos/view/login.fxml"));
                Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                Scene scene = new Scene(root, 1000, 700);
                scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
                stage.setTitle("NexusPOS - Login");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.show();
            }
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
            stage.setMaximized(true);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load: " + fxmlPath);
        }
    }

    // Cart Item data model
    public static class CartItem {
        private final String productName;
        private final double price;
        private final String productCode;
        private int quantity;

        public CartItem(String productName, double price, int quantity, String productCode) {
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
            this.productCode = productCode;
        }

        public String getProductName() { return productName; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getSubtotal() { return price * quantity; }
        public String getProductCode() { return productCode; }
    }

    // Payment result classes
    private static class PaymentResult {
        private final String paymentType;
        private final double amount;

        public PaymentResult(String paymentType, double amount) {
            this.paymentType = paymentType;
            this.amount = amount;
        }

        public String getPaymentType() { return paymentType; }
        public double getAmount() { return amount; }
    }

    private static class SplitPaymentResult {
        private final double cashAmount;
        private final double cardAmount;

        public SplitPaymentResult(double cashAmount, double cardAmount) {
            this.cashAmount = cashAmount;
            this.cardAmount = cardAmount;
        }

        public double getCashAmount() { return cashAmount; }
        public double getCardAmount() { return cardAmount; }
    }
}