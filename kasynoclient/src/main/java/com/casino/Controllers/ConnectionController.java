package com.casino.Controllers;
import com.casino.Connection.ConnectionManager;
import com.casino.Connection.IncomingMessage;
import com.casino.Connection.ServerConnection;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ConnectionController implements IController {
    @FXML
    private Label labelPing;

    @FXML
    private Label labelStatus;

    @FXML
    private TextField textIpAddress;

    @FXML
    private TextField textPort;

    @FXML
    private Button button;

    public ConnectionController() {
    }

    public void onActivate() {

    }

    @FXML
    void buttonConnect(ActionEvent event) {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        if (connectionManager.isConnected()) {
            labelStatus.setText("Status: Disconnecting..");
            connectionManager.disconnect();
            return;
        }
        try {
            connectionManager.connect(textIpAddress.getText(), Integer.parseInt(textPort.getText()));
            labelStatus.setText("Status: Connecting..");
        } catch (Exception e) {
            labelStatus.setText("Status: Failed to connect to server.");
        }
    }

    public void onReceivePing(IncomingMessage msg) {
        Platform.runLater(() -> {
            int ping = msg.getInt();
            labelPing.setText("Ping: " + ping + "ms");
        });
    }

    public void onConnect(){
        labelStatus.setText("Status: Connected!");
        button.setText("Disconnect");

        ServerConnection connection = ConnectionManager.getInstance().GetConnection();
        connection.registerCallback((byte)0x00, (IncomingMessage msg) -> {
            onReceivePing(msg);
        });
        connection.registerCallback((byte)0x01, (IncomingMessage msg) -> {
            LoginController loginController = (LoginController)SceneManager.getInstance().getController("LoginPage");
            loginController.onRegisterCallback(msg);
        });
        connection.registerCallback((byte)0x02, (IncomingMessage msg) -> {
            LoginController loginController = (LoginController)SceneManager.getInstance().getController("LoginPage");
            loginController.onLoginCallback(msg);
        });
        connection.registerCallback((byte)0x06, (IncomingMessage msg) -> {
            GameHistoryController gameHistoryController = (GameHistoryController)SceneManager.getInstance().getController("GameHistory");
            gameHistoryController.onGameHistoryCallback(msg);
        });
    }

    public void onFailedConnect(){
        labelStatus.setText("Status: Failed to connect to server.");
    }

    public void onDisconnect() {
        labelStatus.setText("Status: Disconnected.");
        button.setText("Connect");

        LoginController loginController = (LoginController)SceneManager.getInstance().getController("LoginPage");
        loginController.onDisconnect();
    }

}
