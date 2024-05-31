package com.casino.Controllers;

import javax.swing.Action;

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
    private TextField textValueEmail;
    @FXML
    private PasswordField textValuePassword;

    @FXML
    private Label loginError;

    // Interface methods
    public void onActivate() {
        loginError.setVisible(false);
        loginError.setDisable(true);
        textValueEmail.setText("");
        textValuePassword.setText("");
    }

    // FXML events

    @FXML
    void onCreateAccount(ActionEvent event) {
        //SceneManager.getInstance().activate("RegisterPage");
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
    void onLogin(ActionEvent event) {
        /*if (textValueEmail.getText().isEmpty() || textValuePassword.getText().isEmpty()) {
            loginError.setVisible(true);
            loginError.setDisable(false);
            loginError.setText("Please fill in all the fields.");
            loginError.setTextFill(Paint.valueOf("#ff0000"));
            return;
        }

        UserManager manager = UserManager.getInstance();
        String email = textValueEmail.getText();
        if (manager.CanUserLogin(email, textValuePassword.getText())) {
            User user = manager.GetUserByEmail(email);
            manager.SetCurrentUser(user);
            SceneManager.getInstance().activate("MainPage");
        } else {
            loginError.setVisible(true);
            loginError.setDisable(false);
            loginError.setText("Invalid email or password.");
            loginError.setTextFill(Paint.valueOf("#ff0000"));
        }*/
    }
}
