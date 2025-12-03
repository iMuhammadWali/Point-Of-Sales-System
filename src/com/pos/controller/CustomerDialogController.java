package com.pos.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomerDialogController implements Initializable {

    @FXML private TextField customerIdField;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private Spinner<Integer> loyaltyPointsSpinner;
    @FXML private TextArea notesArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private CustomersController customersController;
    private CustomersController.Customer customerToEdit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupLoyaltyPointsSpinner();
        setupValidation();
    }

    private void setupLoyaltyPointsSpinner() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        loyaltyPointsSpinner.setValueFactory(valueFactory);
    }

    private void setupValidation() {
        // Real-time validation
        nameField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }

    private boolean validateForm() {
        boolean isValid = !customerIdField.getText().trim().isEmpty() &&
                !nameField.getText().trim().isEmpty() &&
                !phoneField.getText().trim().isEmpty() &&
                isValidEmail(emailField.getText().trim());

        saveButton.setDisable(!isValid);
        return isValid;
    }

    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            return true; // Email is optional, so empty is valid
        }
        // Basic email validation - check if contains @ and has proper format
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }

    public void setCustomersController(CustomersController controller) {
        this.customersController = controller;
    }

    public void setCustomerForEditing(CustomersController.Customer customer) {
        this.customerToEdit = customer;
        customerIdField.setText(customer.getCustomerId());
        nameField.setText(customer.getName());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());
        loyaltyPointsSpinner.getValueFactory().setValue(customer.getLoyaltyPoints());
        notesArea.setText(customer.getNotes());

        // Make customer ID read-only when editing
        customerIdField.setDisable(true);
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            showAlert("Validation Error", "Please fill all required fields correctly.");
            return;
        }

        try {
            String customerId = customerIdField.getText().trim();
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            int loyaltyPoints = loyaltyPointsSpinner.getValue();
            String notes = notesArea.getText().trim();

            // Generate total purchases based on loyalty points (dummy logic)
            String totalPurchases = "PKR " + (loyaltyPoints * 10);

            CustomersController.Customer newCustomer =
                    new CustomersController.Customer(customerId, name, phone, email, loyaltyPoints, totalPurchases, notes);

            if (customerToEdit != null) {
                // Update existing customer - just call the method, don't try to use its return value
                customersController.updateCustomer(customerToEdit, newCustomer);
            } else {
                // Add new customer - just call the method, don't try to use its return value
                customersController.addCustomer(newCustomer);
            }

            closeDialog();

        } catch (Exception e) {
            showAlert("Error", "Failed to save customer: " + e.getMessage());
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