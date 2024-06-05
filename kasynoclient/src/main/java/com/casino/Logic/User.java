package com.casino.Logic;

import com.casino.Connection.ConnectionManager;
import com.casino.Connection.IncomingMessage;
import com.casino.Controllers.BlackJackController;
import com.casino.Controllers.HomeController;
import com.casino.Controllers.SceneManager;

import javafx.application.Platform;

public class User {
    private static double balance = 0;


    public static void Setup() {
        ConnectionManager.getInstance().GetConnection().registerCallback((byte)0x05, (IncomingMessage msg) -> {
            onBalanceReceive(msg);
        });
    }

    public static double getBalance() {
        return balance;
    }

    public static void setBalance(double balance) {
        double oldBalance = User.balance;
        User.balance = balance;
        onBalanceUpdate(oldBalance, User.balance);
    }

    public static void onBalanceUpdate(double oldBalance, double newBalance) {
        System.out.println("Balance updated: " + oldBalance + " -> " + newBalance);

        //* Update the UI */
        Platform.runLater(() -> {
            BlackJackController blackJack = SceneManager.getInstance().getController("BlackJackController");
            System.out.println("t1");
            if (blackJack != null) {
                System.out.println("t2");
                blackJack.onBalanceUpdate(newBalance);
            }

            HomeController home = SceneManager.getInstance().getController("HomePage");
            
            if (home != null) {
            
                home.onBalanceUpdate(newBalance);
            }
        });
    }

    public static void onBalanceReceive(IncomingMessage msg) {
        double balance = msg.getDouble();
        setBalance(balance);
    }
}
