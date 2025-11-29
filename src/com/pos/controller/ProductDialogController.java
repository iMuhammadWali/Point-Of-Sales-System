package com.pos.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductDialogController implements Initializable {

    @FXML private TextField productIdField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField priceField;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private ProductsController productsController;
    private ProductsController.Product productToEdit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCategoryComboBox();
        setupQuantitySpinner();
        setupValidationListeners();
        validateForm(); // Initial check to set saveButton state
    }

    private void setupCategoryComboBox() {
        categoryComboBox.getItems().addAll("Beverages", "Snacks", "Dairy", "Bakery", "Electronics", "Other");
    }

    private void setupQuantitySpinner() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0);
        quantitySpinner.setValueFactory(valueFactory);
    }

    private void setupValidationListeners() {
        productIdField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        categoryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        priceField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }


    private boolean validateForm() {
        boolean isValid = !productIdField.getText().trim().isEmpty() &&
                !nameField.getText().trim().isEmpty() &&
                categoryComboBox.getValue() != null &&
                isValidPrice(priceField.getText());

        saveButton.setDisable(!isValid);
        return isValid;
    }

    private boolean isValidPrice(String priceText) {
        try {
            double price = Double.parseDouble(priceText);
            return price >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void setProductsController(ProductsController controller) {
        this.productsController = controller;
    }

    public void setProductForEditing(ProductsController.Product product) {
        this.productToEdit = product;
        productIdField.setText(product.getProductId());
        nameField.setText(product.getName());
        categoryComboBox.setValue(product.getCategory());
        priceField.setText(String.valueOf(product.getPrice()));
        quantitySpinner.getValueFactory().setValue(product.getQuantity());

        // Make product ID read-only when editing
        productIdField.setDisable(true);
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            showAlert("Validation Error", "Please fill all fields correctly.");
            return;
        }

        try {
            String productId = productIdField.getText().trim();
            String name = nameField.getText().trim();
            String category = categoryComboBox.getValue();
            double price = Double.parseDouble(priceField.getText());
            int quantity = quantitySpinner.getValue();

            ProductsController.Product newProduct =
                    new ProductsController.Product(productId, name, category, price, quantity);

            if (productToEdit != null) {
                productsController.updateProduct(productToEdit, newProduct);
            } else {
                productsController.addProduct(newProduct);
            }

            closeDialog();

        } catch (NumberFormatException e) {
            showAlert("Invalid Price", "Please enter a valid price.");
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
