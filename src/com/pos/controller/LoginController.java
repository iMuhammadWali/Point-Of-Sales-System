package com.pos.controller;

import com.pos.database.managers.UserDBManager;
import com.pos.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.pos.database.*;
import com.pos.models.UserSession;

import com.pos.database.DatabaseManager;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;



    @FXML
    private void initialize()  {
        // Go to password field when Enter is pressed in username
        usernameField.setOnAction(e -> {
            passwordField.requestFocus();
        });

        try{
            // Making sure that loginHandler is triggered when Enter-key is pressed when password field is in focus.
            passwordField.setOnAction(e->handleLogin());
        }
        catch (Exception e){
            // Do nothing haha
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try{
            String[] result = DatabaseManager.getInstance().getUserManager().verifyCredentials(username, password);
            //
            if (result == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");

                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password");
                alert.showAndWait();
            }
            else {
                // TODO: Add role-based access
                User user = DatabaseManager.getInstance().getUserManager().getUserDetailsFromUsername(username);
                UserSession session = UserSession.getInstance();
                session.setUserID(user.getUserID());
                session.setUsername(username);
                session.setFullName(user.getFirstName(), user.getLastName());
                session.setRole(user.getRole());

                loadDashboard();
            }
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unexpected Error");
            alert.setHeaderText(null);
            alert.setContentText("An Unexpected Error Occured");
            alert.showAndWait();
        }

    }

    @FXML
    private void handleForgotPassword() {
        // I have no idea how to handle this shit.

        // Optional: show alert for now
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Forgot password clicked!");
        alert.showAndWait();
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pos/view/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow(); // get current stage

            Scene scene = new Scene(root);
            // Load CSS for dashboard
            scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());

            stage.setScene(scene);

            // TODO: When ESC. is pressed when I am on fullscreen, it shows weird behaviour.
            stage.setFullScreen(true);
            stage.show();

        } catch (Exception e) {
//            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load dashboard: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
