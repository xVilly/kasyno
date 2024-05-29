package com.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    private DatabaseConnection() {
        // private constructor to prevent instantiation
    }

    public static Connection getConnection() {
        if (connection == null) {
            synchronized (DatabaseConnection.class) {
                if (connection == null) {
                    try {
                        String url = "jdbc:mysql://localhost:3306/casino-db";
                        String username = "casino-user";
                        String password = "casino-pwd";
                        connection = DriverManager.getConnection(url, username, password);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error connecting to the database", e);
                    }
                }
            }
        }
        return connection;
    }
}