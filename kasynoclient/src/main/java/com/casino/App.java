package com.casino;

import javafx.application.Application;
import com.casino.Controllers.SceneManager;
import javafx.stage.Stage;

import com.casino.Connection.ServerConnection;

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
        ServerConnection server = new ServerConnection("localhost", 9999);
        server.Start();
        launch();
    }
}
