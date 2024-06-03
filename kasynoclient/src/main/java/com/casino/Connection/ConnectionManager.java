package com.casino.Connection;

public class ConnectionManager {

    private static ConnectionManager instance = null;

    private ServerConnection server;

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    private ConnectionManager() {}

    public void connect(String host, int port) {
        server = new ServerConnection(host, port);
        server.Start();
    }

    public ServerConnection GetConnection() {
        return server;
    }
}
