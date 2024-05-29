package com.example.connection;

import java.nio.ByteBuffer;

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

    public void sendAuthenticationResponse(ClientHandler clientHandler, boolean success) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x02, clientHandler);
        msg.putInt(success ? 1 : 0);
        sendMessage(msg);
    }
}
