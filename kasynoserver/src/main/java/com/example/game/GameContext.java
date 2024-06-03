package com.example.game;

import java.util.HashMap;
import java.util.List;

public class GameContext {
    private int id;
    private int type;
    private String user;
    private double bet;

    public GameContext(int id, int type, String user, double bet) {
        this.id = id;
        this.type = type;
        this.user = user;
        this.bet = bet;
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

    
}
