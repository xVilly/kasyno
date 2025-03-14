package com.casino.Controllers;

import javax.swing.Action;

import com.casino.Connection.ConnectionManager;
import com.casino.Connection.IncomingMessage;
import com.casino.Connection.ServerConnection;
import com.casino.Logic.User;

import javafx.application.Platform;

//import com.casino.Logic.Users.User;
//import com.casino.Logic.Users.UserManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

public class LoginController implements IController {

    // FXML fields
    
    @FXML
    private TextField textValueUsername;
    @FXML
    private PasswordField textValuePassword;

    @FXML
    private Label loginError;

    public LoginController() {
        
    }

    // Interface methods
    public void onActivate() {
        loginError.setVisible(false);
        loginError.setDisable(true);
        textValueUsername.setText("");
        textValuePassword.setText("");
    }

    // FXML events

    @FXML
    void onCreateAccount(ActionEvent event) {
        if (textValueUsername.getText().isEmpty() || textValuePassword.getText().isEmpty()) {
            loginError.setVisible(true);
            loginError.setDisable(false);
            loginError.setText("Please fill in all the fields.");
            loginError.setTextFill(Paint.valueOf("#b0453e"));
            return;
        }

        if (!ConnectionManager.getInstance().isConnected()) {
            loginError.setVisible(true);
            loginError.setDisable(false);
            loginError.setText("Please connect to the server first.");
            loginError.setTextFill(Paint.valueOf("#b0453e"));
            return;
        }

        ServerConnection connection = ConnectionManager.getInstance().GetConnection();
        connection.getMessageSender().sendCreateAccount(textValueUsername.getText(), textValuePassword.getText(), "", "", 0);
    }

    @FXML
    void onBlackJack(ActionEvent event) {
        SceneManager.getInstance().activate("BlackJackController");
    }

    @FXML
    void onRoulette(ActionEvent event) {
        SceneManager.getInstance().activate("RouletteController");
    }

    @FXML
    void onConnectionSettings(ActionEvent event) {
        SceneManager.getInstance().openPopupWindow("Connection Settings", "ConnectWindow", "Connection Settings", false, false);
    }

    @FXML
    void onLogin(ActionEvent event) {
        if (textValueUsername.getText().isEmpty() || textValuePassword.getText().isEmpty()) {
            loginError.setVisible(true);
            loginError.setDisable(false);
            loginError.setText("Please fill in all the fields.");
            loginError.setTextFill(Paint.valueOf("#b0453e"));
            return;
        }

        if (!ConnectionManager.getInstance().isConnected()) {
            loginError.setVisible(true);
            loginError.setDisable(false);
            loginError.setText("Please connect to the server first.");
            loginError.setTextFill(Paint.valueOf("#b0453e"));
            return;
        }

        ServerConnection connection = ConnectionManager.getInstance().GetConnection();
        connection.getMessageSender().sendLogin(textValueUsername.getText(), textValuePassword.getText());
    }

    void onLoginCallback(IncomingMessage msg) {
        int result = msg.getInt();
        if (result == 1) {
            Platform.runLater(() -> {
                SceneManager.getInstance().activate("HomePage");
            });
            User.Setup();
            String username = msg.getString();
            double balance = msg.getDouble();
            HomeController controller = (HomeController)SceneManager.getInstance().getController("HomePage");
            if (controller == null) {
                return;
            }
            Platform.runLater(() -> {
                controller.setup(username);
            });

            User.setBalance(balance);
        } else {
            Platform.runLater(() -> {
                loginError.setVisible(true);
                loginError.setDisable(false);
                loginError.setText("Invalid username or password.");
                loginError.setTextFill(Paint.valueOf("#b0453e"));
            });
        }
    }

    void onRegisterCallback(IncomingMessage msg) {
        int result = msg.getInt();
        if (result == 1) {
            Platform.runLater(() -> {
                loginError.setVisible(true);
                loginError.setDisable(false);
                loginError.setText("Account has been created! Now you may log-in.");
                loginError.setTextFill(Paint.valueOf("#07fade"));
                });
        } else {
            Platform.runLater(() -> {
            loginError.setVisible(true);
            loginError.setDisable(false);
            loginError.setText("Failed to create account.");
            loginError.setTextFill(Paint.valueOf("#b0453e"));
            });
        }
    }

    void onDisconnect() {
        Platform.runLater(() -> {
            loginError.setVisible(true);
            loginError.setDisable(false);
            loginError.setText("Disconnected from server.");
            loginError.setTextFill(Paint.valueOf("#b0453e"));
        });
    }
}
