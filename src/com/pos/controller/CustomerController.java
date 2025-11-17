package com.pos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerController {

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    private TableColumn<Customer, String> phoneColumn;

    @FXML
    private TableColumn<Customer, String> emailColumn;

    @FXML
    private Label detailName;

    @FXML
    private Label detailPhone;

    @FXML
    private Label detailEmail;

    @FXML
    private Label detailPurchases;

    private ObservableList<Customer> customers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Dummy data
        customers.addAll(
                new Customer("Alice Smith", "123-456-7890", "alice@example.com", 5),
                new Customer("Bob Johnson", "234-567-8901", "bob@example.com", 2),
                new Customer("Charlie Brown", "345-678-9012", "charlie@example.com", 10)
        );

        customerTable.setItems(customers);

        // Update details panel when a row is selected
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                detailName.setText("Name: " + newSelection.getName());
                detailPhone.setText("Phone: " + newSelection.getPhone());
                detailEmail.setText("Email: " + newSelection.getEmail());
                detailPurchases.setText("Total Purchases: " + newSelection.getPurchases());
            }
        });
    }

    // Dummy Customer class
    public static class Customer {
        private final String name;
        private final String phone;
        private final String email;
        private final int purchases;

        public Customer(String name, String phone, String email, int purchases) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.purchases = purchases;
        }

        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public int getPurchases() { return purchases; }
    }
}
