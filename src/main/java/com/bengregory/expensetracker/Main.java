package com.bengregory.expensetracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*  MVC ~ Model-View-Controller.
    It is a design pattern that separates the application into three interconnected components:

    Model: Manages the data and business logic.
    View: The UI — what the user sees.
    Controller: Handles user interactions, manipulates the model, and updates the view.
 */

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com.bengregory.expensetracker.view/login.fxml"));
        primaryStage.setTitle("Smart Expense Tracker");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
    // Use this command to run the Main file mvn clean javafx:run
    public static void main(String[] args) {
        launch(args);
    }
}