package com.bengregory.expensetracker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage){
        StackPane root = new StackPane();
        root.getChildren().add(new Label("Smart Expense Tracker"));
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Smart Expense Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}