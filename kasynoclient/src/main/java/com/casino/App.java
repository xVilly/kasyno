package com.casino;

import javafx.application.Application;
import com.casino.Controllers.SceneManager;
import javafx.stage.Stage;

import com.casino.Connection.ConnectionManager;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager sceneManager = SceneManager.getInstance();
        primaryStage.setTitle("W&M Casino");
        sceneManager.Initialize(primaryStage);
        sceneManager.LoadScenes();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
