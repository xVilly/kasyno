package com.casino.Connection;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.HashMap;

public class MessageParse {
    private HashMap<Byte, ClientActionCallback> callbacks = new HashMap<>();

    public void setCallback(byte opcode, ClientActionCallback callback) {
        callbacks.put(opcode, callback);
    }

    public void parseMessage(byte[] message, int size) {
        ByteBuffer buffer = ByteBuffer.wrap(message);
        if (size < 1) {
            System.out.println("[casino-client] Received invalid message from the server");
            return;
        }

        // first 1 byte - opcode
        byte opcode = buffer.get();
        IncomingMessage msg = new IncomingMessage(opcode, buffer, size);

        if (callbacks.containsKey(opcode)) {
            callbacks.get(opcode).onReceive(msg);
        } else {
            switch(opcode) {
                default:
                    System.out.println("[casino-client] Received unknown opcode "+opcode+" from the server.");
                    return;
            }
        }

        if (msg.getUnreadSize() > 0) {
            System.out.println("[casino-client] MessageParse left "+msg.getUnreadSize()+" unread bytes (opcode "+opcode+") from the server.");
        }
    }
}
