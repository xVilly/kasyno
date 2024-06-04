package com.example.connection;

import java.nio.ByteBuffer;

import com.example.game.GameContext;
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

    public void sendNewGameResponse(ClientHandler clientHandler, GameContext result) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x03, clientHandler);
        int success = (result != null) ? 1 : 0;
        msg.putInt(success);
        if (success == 1) {
            msg.putInt(result.getId());
        }
        sendMessage(msg);
    }
}
