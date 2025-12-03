package com.pos.main;

import com.pos.database.DBConnection;
import com.pos.database.managers.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import com.pos.controller.*;

public class Main extends Application {
    private DBConnection db;
    private UserDBManager userManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            // Load login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pos/view/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1000, 700);

            // Load CSS
            try {
                scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS not found: " + e.getMessage());
            }

            primaryStage.setTitle("NexusPOS - Login");
            primaryStage.setScene(scene);

            // I should do it everywhere; SetResizeable to false.
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
//            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}