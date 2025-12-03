package com.pos.controller;

import com.pos.models.Product;
import com.pos.models.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.OverrunStyle;

// Sales Transaction manager will also be imported when I create it.
//import com.pos.database.managers.*;

import com.pos.database.DatabaseManager;
import com.pos.database.managers.*;
import com.pos.models.*;
import java.math.BigDecimal;

// TODO: Create a transaction, complete it and save it and it should be shown in the transactions history page.
public class TransactionCreateController implements Initializable {

    // FXML utilities
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
    @FXML private Label totalLabel;
    @FXML private TextField discountField;
    @FXML private TextField loyaltyPointsField;
    @FXML private Label discountAmountLabel;
    @FXML private Label loyaltyDiscountLabel;
    @FXML private Label finalTotalLabel;

    // cartItems (Will be translated to salesItems onces saved to database)
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    // Discount variables.
    private double discountAmount = 0.0;
    private double loyaltyDiscount = 0.0;

    // Added to enhance the search feature.
    @FXML private FlowPane productsFlowPane;
    @FXML private Label InformationBoxLabel;


    private SalesTransactionDBManager transactionManager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Change the welcome message to the current user.
        setupClock();
        setupWelcomeMessage();
        setupCartTable();
        
        // Add listeners to the boxes.
        setupDiscountListeners();
        // This method adds live search functionality as it calls for search again and again.
        setupSearchListener();

        showDefaultProducts();
        // Mark as initialized and update totals
        updateTotals();
        transactionManager = DatabaseManager.getInstance().getSalesTransactionManager();
    }
    private void updateTotals() {
        double subtotal = calculateSubtotal();
        double tax = subtotal * 0.16;
        double total = subtotal + tax;
        double finalTotal = total - discountAmount - loyaltyDiscount;

        // Update all labels (null checks are good practice. I have not added null checks everywhere but adding here.)
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

    // Search Functions
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            showDefaultProducts();
            return;
        }

        productsFlowPane.getChildren().clear();
        List<Product> searchResults = new ArrayList<>();

        try {
            if (searchText.matches("\\d+")) {
                // It is a barcode.
                Product product = DatabaseManager.getInstance().getProductManager().getProductFromBarcode(searchText);
                if (product != null) {
                    searchResults.add(product);
                }
            } else {
                searchResults = DatabaseManager.getInstance().getProductManager().getProductsFromName(searchText);
            }

            for (Product p : searchResults) {
                p.printProduct();
            }
        } catch (Exception e) {
//            e.printStackTrace(); // Better to see the error but I am not seeing it.
        }

        // TODO: Create a VBox for every product returned from the list and add it as children to productsFlowPane
        // Done
        if (searchResults.isEmpty()) {
//             Show "no results" message
            InformationBoxLabel.setText("No results for: '" + searchText + "'");
        } else {
            InformationBoxLabel.setText("Found " + searchResults.size() + " product(s)");
            // Create product cards for each search result
            for (Product product : searchResults) {
                VBox productCard = createProductCard(product);
                productsFlowPane.getChildren().add(productCard);
            }
        }

//        isSearchActive = true;
    }

    // Since I am not a UI Maker, these functions are created by Deepseek to show product Cards.
    private VBox createProductCard(Product product) {
        VBox productCard = new VBox();
        productCard.setAlignment(Pos.CENTER);
        productCard.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 12; -fx-padding: 20; -fx-pref-width: 150; -fx-cursor: hand;");

        // Make the entire card clickable to add product to cart
        productCard.setOnMouseClicked(event -> addProductToCart(product));

        // Product icon/color rectangle
        Rectangle productIcon = new Rectangle(60, 60, getProductColor(product));
        productIcon.setArcWidth(10);
        productIcon.setArcHeight(10);

        // Product name (truncate if too long)
        Label nameLabel = new ProductNameLabel(product.getName());

        // Product price
        Label priceLabel = new Label(String.format("PKR %.2f", product.getPrice()));
        priceLabel.setStyle("-fx-text-fill: #666;");

        // Stock information with color coding
        Label stockLabel = new Label("Stock: " + product.getCurrentStock());
        if (product.getCurrentStock() <= 5) {
            stockLabel.setStyle("-fx-text-fill: #FF6B6B; -fx-font-weight: bold;"); // Red for low stock
        } else if (product.getCurrentStock() <= 10) {
            stockLabel.setStyle("-fx-text-fill: #FFA726;"); // Orange for medium stock
        } else {
            stockLabel.setStyle("-fx-text-fill: #999;"); // Gray for good stock
        }

        // Add all elements to the card
        productCard.getChildren().addAll(productIcon, nameLabel, priceLabel, stockLabel);

        return productCard;
    }
    // Helper class for product name label with ellipsis for long names
    private static class ProductNameLabel extends Label {
        public ProductNameLabel(String text) {
            super(text);
            setStyle("-fx-text-fill: #272848; -fx-padding: 10 0 5 0; -fx-font-size: 14;");
            setMaxWidth(130);
            setWrapText(true);
            setTextOverrun(OverrunStyle.ELLIPSIS);
        }
    }
    private Paint getProductColor(Product product) {
        // Generate a consistent color based on product ID or name hash
        int colorIndex = Math.abs(product.getName().hashCode()) % 4;
        switch (colorIndex) {
            case 0: return Color.web("#54C1FB"); // Blue
            case 1: return Color.web("#6D71F9"); // Purple
            case 2: return Color.web("#4CAF50"); // Green
            case 3: return Color.web("#FF9800"); // Orange
            default: return Color.web("#54C1FB");
        }
    }
    private void showDefaultProducts() {
        if (productsFlowPane == null) return;
        productsFlowPane.getChildren().clear();
        try {
            List<Product> newProducts = DatabaseManager.getInstance().getProductManager()
                    .getNewlyAddedProducts(8);

            for (Product product : newProducts) {
                VBox productCard = createProductCard(product);
                productsFlowPane.getChildren().add(productCard);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        InformationBoxLabel.setText("Quick Items");
    }

    // Setters
    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                showDefaultProducts();
            } else {
                handleSearch();
            }
        });
    }
    private void setupWelcomeMessage() {
        String[] greetings = {"Good morning", "Good afternoon", "Good evening"};
        int hour = java.time.LocalTime.now().getHour();
        String greeting = hour < 12 ? greetings[0] : hour < 18 ? greetings[1] : greetings[2];

        welcomeLabel.setText(greeting + " " + UserSession.getInstance().getFullName());
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
    private void setupDiscountListeners() {
        discountField.textProperty().addListener((observable, oldValue, newValue) -> applyDiscount());
        loyaltyPointsField.textProperty().addListener((observable, oldValue, newValue) -> applyLoyaltyPoints());
    }

    // Cart Functions
    private void setupCartTable() {
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        cartTable.setItems(cartItems);
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
    private void addProductToCart(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getProductID() == product.getProductID()) {
                item.setQuantity(item.getQuantity() + 1);
                cartTable.refresh();
                updateTotals();
                return;
            }
        }

        cartItems.add(new CartItem(product, 1));
        updateTotals();
    }

    // Misc.
    private void updateTime() {
        Platform.runLater(() -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            currentTimeLabel.setText(timeFormat.format(new Date()));
            currentDateLabel.setText(dateFormat.format(new Date()));
        });
    }

    // Completing Transaction functions.
    @FXML
    private void completeTransaction() {
        if (cartItems.isEmpty()) {
            showAlert("Empty Cart", "Please add items to cart before completing transaction.");
            return;
        }

        // Check point
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
        // Checkpoint checking processPayment.
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
        double finalTotal = calculateFinalTotal();
        double subtotal = calculateSubtotal();
        double tax = subtotal * 0.16;

        try {
            // Create SalesTransaction object
            SalesTransaction transaction = createSalesTransaction(finalTotal, tax, paymentType, change);

            // Save to database
            Integer saleId = transactionManager.createSalesTransaction(transaction);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Transaction Complete");

            if (saleId != null && saleId > 0) {
                alert.setHeaderText("Sale Completed Successfully!");
                String paymentInfo = paymentType.equals("SPLIT") ?
                        "\nPayment Method: Split Payment" : "\nPayment Method: " + paymentType;

                alert.setContentText("Transaction #" + saleId + " saved to database!" +
                        "\nChange: PKR " + String.format("%.2f", change) +
                        paymentInfo + "\n\nReceipt has been generated.");

                // Print receipt with transaction ID
                printReceipt(saleId);
            } else {
                alert.setHeaderText("Transaction Issue");
                alert.setContentText("Sale completed but failed to save to database!");
            }

            ButtonType printButton = new ButtonType("Print Receipt");
            ButtonType emailButton = new ButtonType("Email Receipt");
            ButtonType doneButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(printButton, emailButton, doneButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == printButton) {
                    printReceipt(saleId != null ? saleId : 0);
                } else if (buttonType == emailButton) {
                    emailReceipt(saleId != null ? saleId : 0);
                }
                // Clear cart regardless of save status
                clearCartAfterTransaction();
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to save transaction: " + e.getMessage());
        }
    }

    private SalesTransaction createSalesTransaction(double finalTotal, double tax,
                                                    String paymentType, double change) {
        UserSession session = UserSession.getInstance();

        // Create SalesTransaction object
        SalesTransaction transaction = new SalesTransaction();

        // Set transaction details
        transaction.setCashierID(session.getUserID()); // Assuming UserSession has getUserID()
        transaction.setCustomerID(0); // Default customer (or implement customer selection)
        transaction.setShiftID(0); // You might need to track shifts
        transaction.setTotalAmount(BigDecimal.valueOf(calculateSubtotal()));
        transaction.setTaxAmount(BigDecimal.valueOf(tax));
        transaction.setDiscountAmount(BigDecimal.valueOf(discountAmount));
        transaction.setFinalAmount(BigDecimal.valueOf(finalTotal));
        transaction.setStatus("COMPLETED");
        transaction.setCreatedAt(LocalDateTime.now()); // ← THIS IS CRITICAL!

        // Create sale items from cart
        List<SaleItem> saleItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            SaleItem saleItem = new SaleItem();
            saleItem.setProductID(cartItem.getProduct().getProductID());
            saleItem.setQuantity(cartItem.getQuantity());
            saleItem.setUnitPrice(BigDecimal.valueOf(cartItem.getProduct().getPrice()));
            saleItem.setLineTotal(BigDecimal.valueOf(cartItem.getSubtotal()));
            saleItems.add(saleItem);
        }
        transaction.setItems(saleItems);

        // Create payment(s)
        List<Payment> payments = new ArrayList<>();
        Payment payment = new Payment();

        if (paymentType.equals("SPLIT")) {
            // For split payment, you might need to track cash and card amounts separately
            // For now, create a single payment with "SPLIT" method
            payment.setPaymentMethod("SPLIT");
            payment.setAmount(BigDecimal.valueOf(finalTotal));
        } else {
            payment.setPaymentMethod(paymentType);
            payment.setAmount(BigDecimal.valueOf(finalTotal + change)); // Amount paid
        }

        payment.setStatus("COMPLETED");
        payment.setCreatedAt(LocalDateTime.now());
        payments.add(payment);
        transaction.setPayments(payments);

        return transaction;
    }

    private void clearCartAfterTransaction() {
        cartItems.clear();
        discountField.clear();
        loyaltyPointsField.clear();
        discountAmount = 0.0;
        loyaltyDiscount = 0.0;
        updateTotals();
    }

    private void printReceipt(int transactionId) {
        System.out.println("=== NEXUS POS RECEIPT ===");
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println("Cashier: " + UserSession.getInstance().getFullName());
        System.out.println("------------------------");
        for (CartItem item : cartItems) {
            System.out.printf("%s x%d = PKR %.2f%n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getSubtotal());
        }
        System.out.println("------------------------");
        System.out.printf("Subtotal: PKR %.2f%n", calculateSubtotal());
        System.out.printf("Tax (16%%): PKR %.2f%n", calculateSubtotal() * 0.16);
        System.out.printf("Discount: -PKR %.2f%n", discountAmount);
        System.out.printf("Loyalty Discount: -PKR %.2f%n", loyaltyDiscount);
        System.out.printf("TOTAL: PKR %.2f%n", calculateFinalTotal());
        System.out.println("Thank you for your business!");
    }

    private void emailReceipt(int transactionId) {
        showAlert("Email Receipt", "Receipt for transaction #" + transactionId +
                " has been sent to customer's email.");
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
    
    private double calculateSubtotal() {
        return cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    private double calculateFinalTotal() {
        double subtotal = calculateSubtotal();
        double tax = subtotal * 0.16;
        return (subtotal + tax) - discountAmount - loyaltyDiscount;
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
        navigateTo("/com/pos/view/reports.fxml");
    }

    @FXML
    private void showSettings() {
        navigateTo("/com/pos/view/settings.fxml");
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


    // I am planning on using this CartItem class as it is, for this page.
    // Cart Item helper model class
    public static class CartItem {
        private final Product product;  // Store the actual product object
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        // Keep these getters for TableView compatibility
        public String getProductName() { return product.getName(); }
        public double getPrice() { return product.getPrice(); }
        public double getSubtotal() { return product.getPrice() * quantity; }
        public String getProductCode() {
            return product.getBarcode() != null ? product.getBarcode() : "N/A";
        }
        public int getProductId() { return product.getProductID(); } // For database saving
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