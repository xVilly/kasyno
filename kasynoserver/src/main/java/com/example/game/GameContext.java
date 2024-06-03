package com.example.game;

import java.util.HashMap;
import java.util.List;

public class GameContext {
    private int id;
    private int type;
    private String user;
    private HashMap<String, Integer> bets;

    public GameContext(int id, int type, String user, double bet) {
        this.id = id;
        this.type = type;
        this.user = user;
        this.bets = bets;
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

    public HashMap<String, Integer> getBets() {
        return bets;
    }

    public void setBets(HashMap<String, Integer> bets) {
        this.bets = bets;
    }

    
}
