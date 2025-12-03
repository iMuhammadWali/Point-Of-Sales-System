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
import javafx.stage.Modality;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;


// TODO: I need to change the product to my own model.
// TODO: I need to display all the products from the database in the products table and then use the edit button to change its stock.

public class ProductsController implements Initializable {

    @FXML private Label currentTimeLabel;
    @FXML private Label currentDateLabel;
    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> stockFilter;
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> colProductId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, String> colStatus;
    @FXML private TableColumn<Product, String> colSupplier;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ObservableList<Product> products = FXCollections.observableArrayList();
    private ObservableList<Product> filteredProducts = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupClock();
        setupWelcomeMessage();
        setupFilters();
        setupProductsTable();
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

    private void setupFilters() {
        // Category filter
        categoryFilter.getItems().addAll("All Categories", "Beverages", "Snacks", "Dairy", "Bakery", "Electronics", "Stationery");
        categoryFilter.setValue("All Categories");

        // Stock filter
        stockFilter.getItems().addAll("All Stock", "In Stock", "Low Stock", "Out of Stock");
        stockFilter.setValue("All Stock");

        // Add listeners for real-time filtering
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterProducts());
        categoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterProducts());
        stockFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterProducts());
    }

    private void setupProductsTable() {
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        //colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        productsTable.setItems(filteredProducts);

        // Custom cell factory for status column to show colors
        colStatus.setCellFactory(column -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Low Stock":
                            setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                            break;
                        case "Out of Stock":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupTableSelectionListener() {
        productsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    boolean itemSelected = newValue != null;
                    editButton.setDisable(!itemSelected);
                    deleteButton.setDisable(!itemSelected);
                }
        );
    }

    private void loadDummyData() {
        // Enhanced dummy data with suppliers and varied stock levels
        products.addAll(
                new Product("P-001", "Coca Cola 330ml", "Beverages", 1.50, 25, "Beverage Distributors"),
                new Product("P-002", "Lays Classic Chips", "Snacks", 2.00, 8, "Snack Foods Ltd"),
                new Product("P-003", "Mineral Water 500ml", "Beverages", 0.75, 0, "Aqua Pure"),
                new Product("P-004", "Chocolate Bar", "Snacks", 1.25, 15, "Sweet Treats Inc"),
                new Product("P-005", "Fresh Milk 1L", "Dairy", 2.50, 5, "Dairy Farms Co"),
                new Product("P-006", "White Bread", "Bakery", 1.00, 12, "City Bakery"),
                new Product("P-007", "Cheddar Cheese 200g", "Dairy", 3.75, 20, "Dairy Farms Co"),
                new Product("P-008", "Apple Juice 1L", "Beverages", 2.25, 30, "Beverage Distributors"),
                new Product("P-009", "Potato Chips", "Snacks", 1.80, 3, "Snack Foods Ltd"),
                new Product("P-010", "Yogurt 150g", "Dairy", 1.50, 18, "Dairy Farms Co"),
                new Product("P-011", "Croissant", "Bakery", 1.20, 7, "City Bakery"),
                new Product("P-012", "Orange Juice 500ml", "Beverages", 1.75, 22, "Beverage Distributors"),
                new Product("P-013", "USB Cable", "Electronics", 4.50, 15, "Tech Supplies"),
                new Product("P-014", "Notebook", "Stationery", 2.50, 40, "Office World"),
                new Product("P-015", "Pen Set", "Stationery", 3.00, 35, "Office World")
        );
        filteredProducts.setAll(products);
    }

    private void filterProducts() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = categoryFilter.getValue();
        String selectedStock = stockFilter.getValue();

        ObservableList<Product> filtered = FXCollections.observableArrayList();

        for (Product product : products) {
            boolean matchesSearch = searchText.isEmpty() ||
                    product.getName().toLowerCase().contains(searchText) ||
                    product.getProductId().toLowerCase().contains(searchText) ||
                    product.getSupplier().toLowerCase().contains(searchText);

            boolean matchesCategory = selectedCategory.equals("All Categories") ||
                    product.getCategory().equals(selectedCategory);

            boolean matchesStock = selectedStock.equals("All Stock") ||
                    (selectedStock.equals("In Stock") && product.getQuantity() > 10) ||
                    (selectedStock.equals("Low Stock") && product.getQuantity() > 0 && product.getQuantity() <= 10) ||
                    (selectedStock.equals("Out of Stock") && product.getQuantity() == 0);

            if (matchesSearch && matchesCategory && matchesStock) {
                filtered.add(product);
            }
        }

        filteredProducts.setAll(filtered);

        // Update low stock count for dashboard
        updateLowStockCount();
    }

    private void updateLowStockCount() {
        long lowStockCount = products.stream()
                .filter(p -> p.getQuantity() > 0 && p.getQuantity() <= 10)
                .count();
        // This could be sent back to dashboard if needed
        System.out.println("Low stock items: " + lowStockCount);
    }

    @FXML
    private void handleSearch() {
        filterProducts();
    }

    @FXML
    private void handleAddProduct() {
        showProductDialog(null);
    }

    @FXML
    private void handleEditProduct() {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            showProductDialog(selectedProduct);
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Product");
            alert.setHeaderText("Delete " + selectedProduct.getName() + "?");
            alert.setContentText("Are you sure you want to delete this product? This action cannot be undone.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    products.remove(selectedProduct);
                    filterProducts();
                    showAlert("Success", "Product deleted successfully!");
                }
            });
        }
    }

    private void showProductDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pos/view/product-dialog.fxml"));
            Parent root = loader.load();

            ProductDialogController dialogController = loader.getController();
            dialogController.setProductsController(this);
            if (product != null) {
                dialogController.setProductForEditing(product);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(product == null ? "Add New Product" : "Edit Product");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(welcomeLabel.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open product dialog: " + e.getMessage());
        }
    }

    public void addProduct(Product product) {
        products.add(product);
        filterProducts();
        showAlert("Success", "Product added successfully!");
    }

    public void updateProduct(Product oldProduct, Product updatedProduct) {
        int index = products.indexOf(oldProduct);
        if (index != -1) {
            products.set(index, updatedProduct);
            filterProducts();
            showAlert("Success", "Product updated successfully!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
//            e.printStackTrace();
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
        // Already on products page
    }

    @FXML
    private void showCustomers() {
        navigateTo("/com/pos/view/customers.fxml");
    }

    @FXML
    private void showSettings() {
        navigateTo("/com/pos/view/settings.fxml");
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
            Scene scene = new Scene(root);
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

    // Enhanced Product data model with supplier
    public static class Product {
        private final String productId;
        private final String name;
        private final String category;
        private final double price;
        private final int quantity;
        private final String supplier;

        public Product(String productId, String name, String category, double price, int quantity, String supplier) {
            this.productId = productId;
            this.name = name;
            this.category = category;
            this.price = price;
            this.quantity = quantity;
            this.supplier = supplier;
        }

        public Product(String productId, String name, String category, double price, int quantity) {
            this(productId, name, category, price, quantity, "Default Supplier");
        }

        public String getProductId() { return productId; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public String getSupplier() { return supplier; }
        public String getStatus() {
            if (quantity == 0) return "Out of Stock";
            if (quantity <= 10) return "Low Stock";
            return "In Stock";
        }
    }
}