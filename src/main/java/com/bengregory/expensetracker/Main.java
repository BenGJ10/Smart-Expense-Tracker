package com.bengregory.expensetracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/login.fxml"));
        primaryStage.setTitle("Smart Expense Tracker");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }
    // Use this command to run the Main file mvn clean javafx:run
    public static void main(String[] args) {
        launch(args);
    }
}