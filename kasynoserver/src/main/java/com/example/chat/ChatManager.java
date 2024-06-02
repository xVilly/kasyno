package com.example.chat;

import java.util.ArrayList;
import java.util.List;

import com.example.connection.ClientHandler;
import com.example.connection.ConnectionManager;

public class ChatManager {

    private static ChatManager instance;
    private ChatManager() {
        // private constructor to prevent instantiation
    }

    public static ChatManager getInstance() {
        if (instance == null) {
            synchronized (ChatManager.class) {
                if (instance == null) {
                    instance = new ChatManager();
                }
            }
        }
        return instance;
    }

    private List<String> users = new ArrayList<>();

    public void sendMessageToUser(String user, String message, int messageType) {
        List<ClientHandler> clientConnections = ConnectionManager.getInstance().getUserConnections(user);
        for (ClientHandler clientHandler : clientConnections) {
            ConnectionManager.getInstance().getMessageSender().sendChatMessage(clientHandler, message, messageType);
        }
    }
    
    public void sendMessage(String message, int messageType) {
        for (String user : users) {
            sendMessageToUser(user, message, messageType);
        }
    }

    public void sendSystemMessage(String message) {
        sendMessage(message, 0);
    }

    public void sendUserMessage(String message, String sender) {
        for (String user : users) {
            if (user.equals(sender))
                sendMessageToUser(user, "You: " + message, 1);
            else {
                sendMessageToUser(user, sender + ": " + message, 2);
            }
        }
    }

    public void sendLocalMessage(String user, String message) {
        sendMessageToUser(user, message, 3);
    }

    public void onUserJoin(String username) {
        if (!users.contains(username)){
            users.add(username);
            String userList = String.join(", ", users);
            sendLocalMessage(username, "Welcome to the chat! Online users: " + userList);            
            sendSystemMessage(username + " has joined the chat!");
        }
    }

    public void onUserMessage(String username, String message) {
        sendUserMessage(message, username);
    }
}
