package com.example.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.user.UserManager;

public class GameManager {
    private static GameManager instance;

    public List<GameContext> games;
    private int lastGameId = -1;

    private GameManager() {
        // private constructor to prevent instantiation
    }

    public static GameManager getInstance() {
        if (instance == null) {
            synchronized (GameManager.class) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }

    public int startGame(int gameType) {
        lastGameId++;
        GameContext newGame = new GameContext(lastGameId, gameType, new ArrayList<>(), new HashMap<>());
        games.add(newGame);
        return lastGameId;
    }

    public void joinGame(int gameId, String playerName, int betAmount) {
        GameContext game = games.stream().filter(g -> g.getId() == gameId).findFirst().orElse(null);
        if (game != null) {
            game.getPlayers().add(playerName);
            game.getBets().put(playerName, betAmount);
            UserManager.getInstance().UpdateUserBalance(playerName, -betAmount);
        }
    }

    public void endGame(int gameId) {
        games.remove(gameId);
    }
}
