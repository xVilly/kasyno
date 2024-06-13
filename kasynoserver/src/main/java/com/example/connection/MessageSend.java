package com.example.connection;

import java.nio.ByteBuffer;
import java.util.List;

import com.example.game.GameContext;
import com.example.game.structures.StartGameResult;
import com.example.user.LoginResponse;

public class MessageSend {
    public void sendMessage(OutgoingMessage message) {
        ClientHandler sender = message.getSender();
        ByteBuffer buffer = message.getBuffer();
        if (sender != null && buffer != null) {
            buffer.flip();
            byte[] bytesToSend = new byte[buffer.remaining()];
            buffer.get(bytesToSend);
            sender.sendPacket(bytesToSend);
        }
        System.out.println("Sent " + message.getBuffer().position() + " bytes to client " + sender.getConnectionId());
    }

    public void sendPingResponse(ClientHandler clientHandler) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x00, clientHandler);
        sendMessage(msg);
    }

    public void sendCreateAccountResponse(ClientHandler clientHandler, boolean success) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x01, clientHandler);
        msg.putInt(success ? 1 : 0);
        sendMessage(msg);
    }

    public void sendAuthenticationResponse(ClientHandler clientHandler, LoginResponse response) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x02, clientHandler);
        msg.putInt(response.getResult());
        if (response.getResult() == 1) {
            msg.putString(response.getUsername());
            msg.putDouble(response.getBalance());
        }
        sendMessage(msg);
    }

    public void sendChatMessage(ClientHandler clientHandler, String message, int messageType) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x03, clientHandler);
        msg.putString(message);
        msg.putInt(messageType);
        sendMessage(msg);
    }

    public void sendNewGameResponse(ClientHandler clientHandler, StartGameResult result) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x04, clientHandler);
        msg.putInt(result.success ? 1 : 0);
        if (result.success) {
            msg.putInt(result.game.getId());
        } else {
            msg.putInt(result.errorCode);
            msg.putString(result.errorMessage);
        }
        sendMessage(msg);
    }

    public void sendUserBalanceUpdate(ClientHandler clientHandler, double balance) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x05, clientHandler);
        msg.putDouble(balance);
        sendMessage(msg);
    }

    public void sendGameHistory(ClientHandler clientHandler, List<GameContext> games) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x06, clientHandler);
        // limit to 10 last games
        if (games.size() > 10) {
            games = games.subList(games.size() - 10, games.size());
        }
        msg.putInt(games.size());
        for (GameContext game : games) {
            msg.putInt(game.getId());
            msg.putInt(game.getType());
            msg.putString(game.getUser());
            msg.putDouble(game.getBet());
            msg.putInt(game.getResult());
            msg.putDouble(game.getBetMultiplier());
            msg.putLong(game.getDate());
        }
        sendMessage(msg);
    }
}
