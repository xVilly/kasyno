package com.example.connection;

import java.nio.ByteBuffer;
import java.sql.Connection;

import com.example.chat.ChatManager;
import com.example.game.GameContext;
import com.example.game.GameManager;
import com.example.game.structures.StartGameResult;
import com.example.user.LoginResponse;
import com.example.user.UserContext;
import com.example.user.UserManager;

public class MessageParse {
    public void parseMessage(byte[] message, int size, ClientHandler clientHandler) {
        ByteBuffer buffer = ByteBuffer.wrap(message);
        if (size < 1) {
            System.out.println("[casino-server] Received invalid message from client '"+clientHandler.getIpAddress()+"' (id "+clientHandler.getConnectionId()+")");
            return;
        }

        // first 1 byte - opcode
        byte opcode = buffer.get();
        IncomingMessage msg = new IncomingMessage(opcode, buffer, size, clientHandler);

        switch(opcode) {
            /* 0x00: Keep Alive (no response) */
            case 0x00:
                parseClientPing(msg);
                break;

            /* 0x01: Create Account */
            case 0x01:
                parseClientCreateAccount(msg);
                break;

            /* 0x02: Login */
            case 0x02:
                parseClientLogin(msg);
                break;

            /* 0x03: Start New Game */
            case 0x03:
                parseGameStart(msg);
                break;

            /* 0x04: Join Chat */
            case 0x04:
                parseChatJoin(msg);
                break;

            /* 0x05: Chat Message */
            case 0x05:
                parseChatMessage(msg);
                break;

            /* 0x06: End Game */
            case 0x06:
                parseGameEnd(msg);
                break;

            /* 0x07: Buy Chips */
            case 0x07:
                parseBuyChips(msg);
                break;
            
            default:
                System.out.println("[casino-server] Received unknown opcode "+opcode+" from client '"+clientHandler.getIpAddress()+"' (id "+clientHandler.getConnectionId()+")");
                return;
        }

        if (msg.getUnreadSize() > 0) {
            System.out.println("[casino-server] MessageParse left "+msg.getUnreadSize()+" unread bytes (opcode "+opcode+") from client '"+clientHandler.getIpAddress()+"' (id "+clientHandler.getConnectionId()+")");
        }
    }

    private void parseClientPing(IncomingMessage msg) {
        ConnectionManager.getInstance().getMessageSender().sendPingResponse(msg.getSender());
    }


    private void parseClientCreateAccount(IncomingMessage msg) {
        String name = msg.getString();
        String password = msg.getString();
        String mail = msg.getString();
        String address = msg.getString();
        int age = msg.getInt();

        int result = UserManager.getInstance().CreateAccount(name, password, address, mail, age);
        ConnectionManager.getInstance().getMessageSender().sendCreateAccountResponse(msg.getSender(), result == 1);
    }

    private void parseClientLogin(IncomingMessage msg) {
        String name = msg.getString();
        String password = msg.getString();
        LoginResponse response = UserManager.getInstance().Authenticate(name, password, msg.getSender());
        if (response.getResult() == 1) {
            msg.getSender().setAssociatedUser(name);
        }
        ConnectionManager.getInstance().getMessageSender().sendAuthenticationResponse(msg.getSender(), response);

        if (response.getResult() == 1) {
            ConnectionManager.getInstance().getMessageSender().sendGameHistory(msg.getSender(), GameManager.getInstance().getGameHistory());
        }
    }

    private void parseGameStart(IncomingMessage msg) {
        int gameType = msg.getInt();
        String username = msg.getSender().getAssociatedUser();
        double betAmount = msg.getDouble();
        if (username.isEmpty()) {
            ConnectionManager.getInstance().getMessageSender().sendNewGameResponse(msg.getSender(), null);
            return;
        }
        
        StartGameResult result = GameManager.getInstance().startGame(gameType, betAmount, username);
        ConnectionManager.getInstance().getMessageSender().sendNewGameResponse(msg.getSender(), result);
    }

    private void parseGameEnd(IncomingMessage msg) {
        int gameId = msg.getInt();
        int result = msg.getInt();
        double betMultiplier = msg.getDouble();
        GameManager.getInstance().endGame(gameId, result, betMultiplier);
    }

    private void parseChatJoin(IncomingMessage msg) {
        String username = msg.getSender().getAssociatedUser();
        if (username == null)
            return;

        ChatManager.getInstance().onUserJoin(username);
    }

    private void parseChatMessage(IncomingMessage msg) {
        String username = msg.getSender().getAssociatedUser();
        if (username == null)
            return;

        String message = msg.getString();
        ChatManager.getInstance().onUserMessage(username, message);
    }

    private void parseBuyChips(IncomingMessage msg) {
        double amount = msg.getDouble();
        String username = msg.getSender().getAssociatedUser();
        if (username == null)
            return;

        UserContext userData = UserManager.getInstance().GetUserData(username);
        UserManager.getInstance().UpdateUserBalance(username, userData.getBalance() + amount);
    }

}
