package com.pos.controller;

import com.pos.main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class TransactionController {

    @FXML
    private TextField searchField;

    @FXML
    private RadioButton barcodeRadio;

    @FXML
    private RadioButton nameRadio;

    @FXML
    private TextField discountField;

    @FXML
    private Button addItemButton;

    @FXML
    private ListView<String> cartListView;

    @FXML
    private Label totalLabel;

    @FXML
    private void handleAddItem(ActionEvent event) {
        String item = searchField.getText();
        if (!item.isEmpty()) {
            cartListView.getItems().add(item);
            updateTotal();
            searchField.clear();
        }
    }

    @FXML
    private void applyDiscount(ActionEvent event) {
        updateTotal();
    }

    @FXML
    private void handleEndTransaction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("End Transaction");
        alert.setHeaderText("Are you sure you want to finish this transaction?");
        alert.setContentText("Click OK to continue or Cancel to stay on this screen.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Main.showDashboardScene(); // switch to dashboard
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTotal() {
        double total = 0;
        for (String item : cartListView.getItems()) {
            total += 10; // placeholder for item price
        }

        try {
            double discount = Double.parseDouble(discountField.getText());
            total -= discount;
        } catch (NumberFormatException ignored) {
        }

        totalLabel.setText("Total: $" + total);
    }
}
