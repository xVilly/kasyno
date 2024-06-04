package com.example.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    newGame.setId(id);
                    return newGame;
                }
            }
            return null;
        } catch (SQLException e) {
            System.out.println("[casino-server] Failed to insert a new game: " + e.getMessage());
            return null;
        } finally {
        }
    }

    public GameContext startGame(int gameType, double betAmount, String username) {
        GameContext createdGame = createNewGame(gameType, betAmount, username);
        if (createdGame == null) {
            System.out.println("[casino-server] Failed to start a new game type " + gameType + " for user " + username);
            return null;
        }
        games.add(createdGame);
        return createdGame;
    }

    public GameContext onEndGame(int gameId, int result) {
        return null;
    }
}
