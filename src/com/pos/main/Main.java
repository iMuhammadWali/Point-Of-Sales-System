package com.pos.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginScene();
        stage.setTitle("POS System");
        stage.show();
    }

    // Login Scene
    public static void showLoginScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    // Transaction Scene
    public static void showTransactionScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/transaction.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    // Dashboard Scene
    public static void showDashboardScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/dashboard.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    public static void showCustomerScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/customer.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}
