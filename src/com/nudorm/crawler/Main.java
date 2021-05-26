package com.nudorm.crawler;

import com.nudorm.crawler.controller.Crawler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Crawler.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Crawler");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> {
            ((Crawler)loader.getController()).getCoordinator().quit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
