package com.casino.Connection;

import java.nio.ByteBuffer;
import java.sql.Connection;

public class MessageParse {
    public void parseMessage(byte[] message, int size) {
        ByteBuffer buffer = ByteBuffer.wrap(message);
        if (size < 1) {
            System.out.println("[casino-client] Received invalid message from the server");
            return;
        }

        // first 1 byte - opcode
        byte opcode = buffer.get();
        IncomingMessage msg = new IncomingMessage(opcode, buffer, size);

        switch(opcode) {

            /* 0x01: Create account result */
            case 0x01:
                parseServerCreateAccountResult(msg);
                break;

            /* 0x02: Login result */
            case 0x02:
                parseServerLoginResult(msg);
                break;
            
            default:
                System.out.println("[casino-client] Received unknown opcode "+opcode+" from the server.");
                return;
        }

        if (msg.getUnreadSize() > 0) {
            System.out.println("[casino-client] MessageParse left "+msg.getUnreadSize()+" unread bytes (opcode "+opcode+") from the server.");
        }
    }


    public void parseServerCreateAccountResult(IncomingMessage msg) {
        int result = msg.getInt();
        if (result == 1) {
            System.out.println("[casino-client] Account created successfully.");
        } else {
            System.out.println("[casino-client] Failed to create account.");
        }
    }

    public void parseServerLoginResult(IncomingMessage msg) {
        int result = msg.getInt();
        if (result == 1) {
            System.out.println("[casino-client] Login successful.");
        } else {
            System.out.println("[casino-client] Login failed.");
        }
    }
}
