package com.pos.controller;
import com.pos.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductsController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, Double> discountColumn;

    private ObservableList<Product> products = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up dummy data
        products.addAll(
                new Product("Apple", 50, 1.0, 0.0),
                new Product("Orange", 30, 1.5, 0.0),
                new Product("Banana", 40, 0.5, 0.0)
        );

        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        discountColumn.setCellValueFactory(cellData -> cellData.getValue().discountProperty().asObject());

        productTable.setItems(products);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            productTable.setItems(products);
            return;
        }

        ObservableList<Product> filtered = FXCollections.observableArrayList();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(searchText)) {
                filtered.add(p);
            }
        }
        productTable.setItems(filtered);
    }

    @FXML
    private void handleEditQuantity(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getQuantity()));
            dialog.setTitle("Edit Quantity");
            dialog.setHeaderText("Edit Quantity for " + selected.getName());
            dialog.setContentText("Enter new quantity:");

            dialog.showAndWait().ifPresent(input -> {
                try {
                    int newQty = Integer.parseInt(input);
                    selected.setQuantity(newQty);
                    productTable.refresh();
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid number entered!", ButtonType.OK);
                    alert.showAndWait();
                }
            });
        }
    }

    @FXML
    private void handleApplyDiscount(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getDiscount()));
            dialog.setTitle("Apply Discount");
            dialog.setHeaderText("Apply Discount for " + selected.getName());
            dialog.setContentText("Enter discount amount:");

            dialog.showAndWait().ifPresent(input -> {
                try {
                    double discount = Double.parseDouble(input);
                    selected.setDiscount(discount);
                    productTable.refresh();
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid number entered!", ButtonType.OK);
                    alert.showAndWait();
                }
            });
        }
    }
}
