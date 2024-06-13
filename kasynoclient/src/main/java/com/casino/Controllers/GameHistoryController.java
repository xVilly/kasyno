package com.casino.Controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.casino.Connection.ConnectionManager;
import com.casino.Connection.IncomingMessage;
import com.casino.Connection.ServerConnection;
import com.casino.Logic.Game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class GameHistoryController implements IController {

    @FXML
    private VBox gameHistoryBox;

    @FXML
    private ScrollPane scrollPane;

    public GameHistoryController() {
        
    }

    public void onGameHistoryCallback(IncomingMessage msg) {
        List<Game> games = new ArrayList<>();
        int size = msg.getInt();
        for (int i = 0; i < size; i++) {
            Game game = new Game();
            game.id = msg.getInt();
            game.gameType = msg.getInt();
            game.user = msg.getString();
            game.betAmount = msg.getDouble();
            game.result = msg.getInt();
            game.betMultiplier = msg.getDouble();
            game.date = new Date(msg.getLong() * 1000);
            games.add(game);
        }
        Platform.runLater(() -> UpdateGames(games.reversed()));
    }

    public void UpdateGames(List<Game> games) {
        gameHistoryBox.getChildren().clear();
        for (Game game : games) {
            renderGame(game);
        }
    }

    public void onActivate() {

    }

    private void renderGame(Game game) {
        AnchorPane gamePane = new AnchorPane();
        gamePane.setStyle("-fx-border-radius: 3; -fx-border-width: 1;");
        gamePane.setPrefWidth(370);
        gamePane.setPrefHeight(70);
        VBox.setMargin(gamePane, new Insets(2, 2, 1, 2));


        if (game.gameType == 1) {
            gamePane.setStyle("-fx-border-radius: 3; -fx-border-width: 1; -fx-border-color: #081978;");
        } else if (game.gameType == 2) {
            gamePane.setStyle("-fx-border-radius: 3; -fx-border-width: 1; -fx-border-color: #6e2e04;");
        
        }

        VBox leftGameInfo = new VBox();
        leftGameInfo.setLayoutX(10);
        leftGameInfo.setLayoutY(10);

        Label gameType = new Label(game.gameType == 1 ? "BlackJack" : "Roulette");
        gameType.setFont(new javafx.scene.text.Font("Consolas", 22));
        Label gameUser = new Label("Played by " + game.user + " on " + game.date);
        gameUser.setStyle("-fx-text-fill: #d5b3ff");
        gameUser.setFont(new javafx.scene.text.Font("Consolas", 16));
        Label gameBetAmount = new Label("Bet Amount: " + game.betAmount);
        gameBetAmount.setStyle("-fx-text-fill: #d5b3ff");
        gameBetAmount.setFont(new javafx.scene.text.Font("Consolas", 16));
        Label gameResult = new Label();
        if (game.result == 1) {
            gameResult.setText("Result: Win (" + game.betMultiplier + "x)");
            gameResult.setStyle("-fx-text-fill: #e3cd09;");
        } else if (game.result == 2) {
            gameResult.setText("Result: Loss (" + game.betMultiplier + "x)");
            gameResult.setStyle("-fx-text-fill: #e33f09;");
        } else if (game.result == 3) {
            gameResult.setText("Result: Draw (" + game.betMultiplier + "x)");
            gameResult.setStyle("-fx-text-fill: #99a2ff;");
        } else {
            gameResult.setText("Result: In progress..");
            gameResult.setStyle("-fx-text-fill: #ccfff7");
        }
        gameResult.setFont(new javafx.scene.text.Font("Consolas", 18));

        leftGameInfo.getChildren().addAll(gameType, gameUser, gameBetAmount, gameResult);

        gamePane.getChildren().add(leftGameInfo);

        gameHistoryBox.getChildren().add(gamePane);

        scrollPane.setContent(gameHistoryBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setDisable(false);
    }

}
