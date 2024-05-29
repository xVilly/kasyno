package com.example.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.spi.DirStateFactory.Result;

import com.example.connection.ClientHandler;
import com.example.connection.ConnectionManager;
import com.example.database.DatabaseConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserManager {
    private static UserManager instance;

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    private UserManager() {}

    public int CreateAccount(String username, String password, String address, String mail, int age) {

        try {
            Connection db = DatabaseConnection.getConnection();
            String sql = "INSERT INTO `user-accounts` (id, name, password, email, address, age) VALUES (NULL, ?, ?, ?, ?, ?)";
            PreparedStatement statement = db.prepareStatement(sql);

            statement.setString(1, username);
            String hashedPassword = hashPassword(password);
            statement.setString(2, hashedPassword);
            statement.setString(3, mail);
            statement.setString(4, address);
            statement.setInt(5, age);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[casino-server] User '"+username+"' account created successfully");
                return 1;
            } else {
                System.out.println("[casino-server] Failed to create user '"+username+"' account");
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("[casino-server] Failed to create user '"+username+"' account: " + e.getMessage());
            return 0;
        }
    }

    public int Authenticate(String username, String password, ClientHandler clientHandler) {
        try {
            Connection db = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM `user-accounts` WHERE name = ? AND password = ?";
            PreparedStatement statement = db.prepareStatement(sql);

            statement.setString(1, username);
            String hashedPassword = hashPassword(password);
            statement.setString(2, hashedPassword);
            
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                System.out.println("[casino-server] User '"+username+"' authenticated successfully");
                return 1;
            } else {
                System.out.println("[casino-server] User '"+username+"' failed to authenticate");
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("[casino-server] Failed to authenticate user '"+username+"': " + e.getMessage());
            return 0;
        }
    }

    public int UpdateUserBalance(String username, int balance) {
        try {
            Connection db = DatabaseConnection.getConnection();
            String sql = "UPDATE `user-accounts` SET balance = ? WHERE name = ?";
            PreparedStatement statement = db.prepareStatement(sql);

            statement.setInt(1, balance);
            statement.setString(2, username);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[casino-server] User '"+username+"' balance updated successfully");
                return 1;
            } else {
                System.out.println("[casino-server] Failed to update user '"+username+"' balance");
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("[casino-server] Failed to update user '"+username+"' balance: " + e.getMessage());
            return 0;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password: " + e.getMessage());
        }
    }

}
