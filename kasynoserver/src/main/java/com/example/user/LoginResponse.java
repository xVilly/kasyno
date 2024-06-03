package com.example.user;

public class LoginResponse {
    private int result;
    private String username;
    private double balance;

    public LoginResponse(int result, String username, double balance) {
        this.result = result;
        this.username = username;
        this.balance = balance;
    }

    public LoginResponse(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }
}
