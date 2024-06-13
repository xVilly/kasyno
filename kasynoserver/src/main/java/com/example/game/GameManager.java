package com.example.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.connection.ClientHandler;
import com.example.connection.ConnectionManager;
import com.example.database.DatabaseConnection;
import com.example.user.UserContext;
import com.example.user.UserManager;

import com.example.game.structures.*;

public class GameManager {
    private static GameManager instance;

    public List<GameContext> games = new ArrayList<GameContext>();

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

    public GameContext getGameById(int gameId)
    {
        for (GameContext game : games) {
            if (game.getId() == gameId) {
                return game;
            }
        }
        return null;
    }

    public GameContext createNewGame(int gameType, double betAmount, String user) {
        GameContext newGame = new GameContext(-1, gameType, user, betAmount);
        try {
            Connection db = DatabaseConnection.getConnection();
            String sql = "INSERT INTO `game-history` (id, type, user, bet, result, date) VALUES (NULL, ?, ?, ?, 0, ?)";
            PreparedStatement statement = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

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
            System.out.println("[casino-server] createNewGame: Failed to insert a new game: " + e.getMessage());
            return null;
        }
    }

    public boolean updateGame(GameContext game) {
        try {
            Connection db = DatabaseConnection.getConnection();
            String sql = "UPDATE `game-history` SET result = ?, date = ?, betMultiplier = ? WHERE id = ?";
            PreparedStatement statement = db.prepareStatement(sql);

            statement.setInt(1, game.getResult());
            statement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            statement.setDouble(3, game.getBetMultiplier());
            statement.setInt(4, game.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("[casino-server] updateGame: Failed to update game " + game.getId() + ": " + e.getMessage());
            return false;
        }
    }

    public StartGameResult startGame(int gameType, double betAmount, String username) {

        //* StartGame: Pre-checks (User exists check, User balance check) */
        UserManager userManager = UserManager.getInstance();
        UserContext userContext = userManager.GetUserData(username);
        if (userContext == null) {
            System.out.println("[casino-server] startGame: User " + username + " not found");
            return new StartGameResult(1, "User not found");
        }

        if (userContext.getBalance() < betAmount) {
            System.out.println("[casino-server] startGame: User " + username + " has insufficient balance");
            return new StartGameResult(2, "Insufficient balance");
        }

        //* StartGame - Action Execution */

        GameContext createdGame = createNewGame(gameType, betAmount, username);
        if (createdGame == null) {
            System.out.println("[casino-server] startGame: Failed to start a new game type " + gameType + " for user " + username);
            return new StartGameResult(3, "Failed to start a new game");
        }
        games.add(createdGame);

        //* StartGame - Post Actions (User balance update) */
        userManager.UpdateUserBalance(username, userContext.getBalance() - betAmount);
        onGameHistoryUpdate();

        return new StartGameResult(createdGame);
    }

    public boolean endGame(int gameId, int result, double betMultiplier) {
        //* EndGame: Pre-checks */
        GameContext game = getGameById(gameId);
        if (game == null) {
            System.out.println("[casino-server] endGame: Game " + gameId + " not found");
            return false;
        }

        //* EndGame: Action Execution */
        game.setResult(result);
        game.setBetMultiplier(betMultiplier);
        if (!updateGame(game)) {
            System.out.println("[casino-server] endGame: Failed to update game " + gameId);
            return false;
        }
        games.remove(game);

        //* EndGame: Post Actions */
        System.out.println("[casino-server] endGame: Game " + gameId + " ended with result " + result + " and bet multiplier " + betMultiplier);
        UserManager userManager = UserManager.getInstance();
        UserContext userContext = userManager.GetUserData(game.getUser());
        userManager.UpdateUserBalance(game.getUser(), userContext.getBalance() + game.getBet() * betMultiplier);
        onGameHistoryUpdate();

        return true;
    }

    public List<GameContext> getGameHistory() {
        List<GameContext> gameHistory = new ArrayList<GameContext>();
        try {
            Connection db = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM `game-history`";
            PreparedStatement statement = db.prepareStatement(sql);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                GameContext game = new GameContext(result.getInt("id"), result.getInt("type"), result.getString("user"), result.getDouble("bet"));
                game.setResult(result.getInt("result"));
                game.setBetMultiplier(result.getDouble("betMultiplier"));
                game.setDate(result.getDate("date").toLocalDate().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC));
                gameHistory.add(game);
            }
        } catch (SQLException e) {
            System.out.println("[casino-server] getGameHistory: Failed to retrieve game history: " + e.getMessage());
        }
        return gameHistory;
    }

    public void onGameHistoryUpdate() {
        List<GameContext> gameHistory = getGameHistory();
        for (ClientHandler client : ConnectionManager.getInstance().getClientConnections()) {
            ConnectionManager.getInstance().getMessageSender().sendGameHistory(client, gameHistory);
        }
    }
}
