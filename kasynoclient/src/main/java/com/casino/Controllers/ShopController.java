package com.casino.Controllers;

import com.casino.Connection.ConnectionManager;
import com.casino.Connection.ServerConnection;

import javafx.fxml.FXML;

public class ShopController implements IController {
    public void onActivate() {

    }

    @FXML
    public void onFiveChip() {
        ServerConnection connection = ConnectionManager.getInstance().GetConnection();
        connection.getMessageSender().sendBuyChips(5);
    }

    @FXML
    public void onTwentyChip() {
        ServerConnection connection = ConnectionManager.getInstance().GetConnection();
        connection.getMessageSender().sendBuyChips(20);
    }

    @FXML
    public void onHundredChip() {
        ServerConnection connection = ConnectionManager.getInstance().GetConnection();
        connection.getMessageSender().sendBuyChips(100);
    }
}
