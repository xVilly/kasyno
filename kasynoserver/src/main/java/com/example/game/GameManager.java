package com.example.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.database.DatabaseConnection;
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

    public GameContext createNewGame(int gameType, double betAmount, String user) {
        GameContext newGame = new GameContext(-1, gameType, user, betAmount);
        try {
            Connection db = DatabaseConnection.getConnection();
            String sql = "INSERT INTO `game-history` (id, type, user, bet, result, date) VALUES (NULL, ?, ?, ?, 0, ?)";
            PreparedStatement statement = db.prepareStatement(sql);

            statement.setInt(1, gameType);
            statement.setString(2, user);
            statement.setDouble(3, betAmount);
            statement.setDate(4, new java.sql.Date(System.currentTimeMillis()));


            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[casino-server] Game ");
                return 1;
            } else {
                System.out.println("[casino-server] Failed to create user '"+username+"' account");
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("[casino-server] Failed to create user '"+username+"' account: " + e.getMessage());
            return 0;
        }
        games.add(newGame);
        return newGame;
    }

    public int startGame(int gameType, double betAmount) {
        
        GameContext newGame = new GameContext(lastGameId, gameType,);
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
