package com.casino.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class HomeController implements IController {

    @FXML
    private Label labelBalance;

    @FXML
    private Label labelUsername;

    @FXML
    private GridPane gridBlackjack;

    @FXML
    private GridPane gridRoulette;

    @FXML
    private ImageView thumbBlackjack;

    @FXML
    private ImageView thumbRoulette;

    public void onActivate() {
        thumbBlackjack.setFitWidth(gridBlackjack.getWidth());

        thumbRoulette.setFitWidth(gridRoulette.getWidth());
    }

    public void setup(String username) {
        labelUsername.setText(username);
        onBalanceUpdate(0);
    }

    public void onBalanceUpdate(double balance) {
        labelBalance.setText(balance + "");
    }

    @FXML
    void onGameHistory(ActionEvent event) {
        SceneManager.getInstance().openPopupWindow("Game History", "GameHistory", "Game History", true, false);
    }

    @FXML
    void onOpenChat(ActionEvent event) {
        SceneManager.getInstance().openPopupWindow("Casino Chat", "ChatPage", "Casino Chat", true, false);
    }

    @FXML
    void onPlayBlackjack(ActionEvent event) {
        SceneManager.getInstance().openPopupWindow("Casino: BlackJack", "BlackJackController", "Casino: BlackJack", false, false);
    }

    @FXML
    void onPlayRoulette(ActionEvent event) {
        SceneManager.getInstance().openPopupWindow("Casino: Roulette", "RouletteController", "Casino: Roulette", false, false);
    }

    @FXML
    void onShowShop(ActionEvent event) {
        SceneManager.getInstance().openPopupWindow("Buy Chips", "ShopWindow", "Buy Chips", false, false);
    }

}
