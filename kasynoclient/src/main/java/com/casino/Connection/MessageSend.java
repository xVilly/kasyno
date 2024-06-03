package com.casino.Connection;

import java.nio.ByteBuffer;

public class MessageSend {
    private ServerConnection server;

    public MessageSend(ServerConnection server) {
        this.server = server;
    }

    public void sendMessage(OutgoingMessage message) {
        ByteBuffer buffer = message.getBuffer();
        if (server != null && buffer != null) {
            buffer.flip();
            byte[] bytesToSend = new byte[buffer.remaining()];
            buffer.get(bytesToSend);
            server.sendPacket(bytesToSend);
        }
    }

    public void sendCreateAccount(String username, String password, String email, String address, int age) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x01);
        msg.putString(username);
        msg.putString(password);
        msg.putString(email);
        msg.putString(address);
        msg.putInt(age);
        sendMessage(msg);
    }

    public void sendLogin(String username, String password) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x02);
        msg.putString(username);
        msg.putString(password);
        sendMessage(msg);
    }

    public void sendChatJoin() {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x04);
        sendMessage(msg);
    }

    public void sendChatMessage(String message) {
        OutgoingMessage msg = new OutgoingMessage((byte) 0x05);
        msg.putString(message);
        sendMessage(msg);
    }
}
