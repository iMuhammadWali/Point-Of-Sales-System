package com.pos.controller;

import com.pos.main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;   // <-- ADD THIS
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
public class DashboardController {

    @FXML
    public void initialize() {
        // Currently empty, can handle tab events or init logic later
    }

    @FXML
    private void handleStartShift(ActionEvent event) {
        System.out.println("Shift started!");
        // Add actual logic later
    }

    @FXML
    private void handleEndShift(ActionEvent event) {
        boolean confirm = true; // Replace with popup confirmation logic later
        if (confirm) {
            System.out.println("Shift ended!");
        }
    }

    @FXML
    private void handleOpenCustomer(ActionEvent event) {
        try {
            Main.showCustomerScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
