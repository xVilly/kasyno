package com.example.user;

public class UserContext {
    private String username;
    private double balance;

    public UserContext(String username, double balance) {
        this.username = username;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }
}
