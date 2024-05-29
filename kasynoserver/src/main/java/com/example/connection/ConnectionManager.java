package com.example.connection;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.example.user.UserManager;

public class ConnectionManager {
    private static ConnectionManager instance;

    private MessageParse msgParser;
    private MessageSend msgSender;

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    private List<ClientHandler> clientConnections = new ArrayList<>();

    private ConnectionManager() {
        msgParser = new MessageParse();
        msgSender = new MessageSend();
    }

    public void registerClientConnection(ClientHandler clientHandler) throws IllegalArgumentException {
        if (clientHandler == null) {
            throw new IllegalArgumentException("clientHandler cannot be null");
        }
        if (clientConnections.contains(clientHandler)) {
            throw new IllegalArgumentException("clientHandler is already registered");
        }
        clientConnections.add(clientHandler);
        System.out.println("[casino-server] Client connected (id " + clientHandler.getConnectionId()+") from ip "+clientHandler.getIpAddress());
    }

    public void unregisterClientConnection(ClientHandler clientHandler) throws IllegalArgumentException {
        if (clientHandler == null) {
            throw new IllegalArgumentException("clientHandler cannot be null");
        }
        if (!clientConnections.contains(clientHandler)) {
            throw new IllegalArgumentException("clientHandler is not registered");
        }
        clientConnections.remove(clientHandler);
        System.out.println("[casino-server] Client disconnected (id " + clientHandler.getConnectionId()+") from ip "+clientHandler.getIpAddress());
    }

    public void parseClientMessage(byte[] message, int size, ClientHandler clientHandler) {
        msgParser.parseMessage(message, size, clientHandler);
    }

    public MessageSend getMessageSender() {
        return msgSender;
    }

    public void broadcastMessage(byte[] message) {
        for (ClientHandler clientHandler : clientConnections) {

        }
    }
}
