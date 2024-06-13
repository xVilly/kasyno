package com.example.game;

import java.util.HashMap;
import java.util.List;

public class GameContext {
    private int id;
    private int type;
    private String user;
    private double bet;
    // 0 - in progress, 1 - win, 2 - lose
    private int result;
    private double betMultiplier;
    private long date;

    public GameContext(int id, int type, String user, double bet) {
        this.id = id;
        this.type = type;
        this.user = user;
        this.bet = bet;
        this.result = 0;
        this.betMultiplier = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getBet() {
        return bet;
    }

    public void setBet(double bet) {
        this.bet = bet;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public double getBetMultiplier() {
        return betMultiplier;
    }

    public void setBetMultiplier(double betMultiplier) {
        this.betMultiplier = betMultiplier;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDate() {
        return date;
    }
    
}
