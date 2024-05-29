package com.example.connection;

import java.nio.ByteBuffer;
import java.sql.Connection;

import com.example.game.GameManager;
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
            
            default:
                System.out.println("[casino-server] Received unknown opcode "+opcode+" from client '"+clientHandler.getIpAddress()+"' (id "+clientHandler.getConnectionId()+")");
                return;
        }

        if (msg.getUnreadSize() > 0) {
            System.out.println("[casino-server] MessageParse left "+msg.getUnreadSize()+" unread bytes (opcode "+opcode+") from client '"+clientHandler.getIpAddress()+"' (id "+clientHandler.getConnectionId()+")");
        }
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
        int result = UserManager.getInstance().Authenticate(name, password, msg.getSender());
        ConnectionManager.getInstance().getMessageSender().sendAuthenticationResponse(msg.getSender(), result == 1);
    }

    private void parseGameStart(IncomingMessage msg) {
        int gameType = msg.getInt();
        String username = msg.getString();
        int betAmount = msg.getInt();
        int result = GameManager.getInstance().startGame(gameType);
        if (result >= 0) {
            GameManager.getInstance().joinGame(result, username, betAmount);
        }
    }

}
