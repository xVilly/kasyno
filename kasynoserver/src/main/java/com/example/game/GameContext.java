package com.example.game;

import java.util.HashMap;
import java.util.List;

public class GameContext {
    private int id;
    private int type;
    private List<String> players;
    private HashMap<String, Integer> bets;

    public GameContext(int id, int type, List<String> players, HashMap<String, Integer> bets) {
        this.id = id;
        this.type = type;
        this.players = players;
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

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public HashMap<String, Integer> getBets() {
        return bets;
    }

    public void setBets(HashMap<String, Integer> bets) {
        this.bets = bets;
    }

    
}
