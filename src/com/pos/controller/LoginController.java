package com.pos.controller;

import com.pos.main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.equals("admin") && password.equals("1234")) {
            System.out.println("Login successful!");
            try {
                Main.showTransactionScene(); // go to transaction screen
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Login failed!");
        }
    }
}
