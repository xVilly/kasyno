package com.example.connection;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class IncomingMessage {
    private byte opcode;
    private ByteBuffer buffer;
    private int size;
    private ClientHandler sender;

    public IncomingMessage(byte opcode, ByteBuffer buffer, int size, ClientHandler sender) {
        this.opcode = opcode;
        this.buffer = buffer;
        this.size = size;
        this.sender = sender;
    }

    public int getInt() {
        int result = 0;
        try {
            result = buffer.getInt();
        } catch (BufferUnderflowException ex) {
            System.out.println("[casino-server] Buffer underflow on getInt while handling opcode '"+this.opcode+"'.");
        }
        return result;
    }

    public String getString() {
        try {
            int stringSize = buffer.getInt();
            byte[] stringBytes = new byte[stringSize];
            buffer.get(stringBytes);
            return new String(stringBytes);
        }
        catch (BufferUnderflowException ex) {
            System.out.println("[casino-server] Buffer underflow on getString while handling opcode '"+this.opcode+"'.");
            return "";
        }
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public int getUnreadSize() {
        return size - buffer.position();
    }

    public int getSize() {
        return size;
    }

    public ClientHandler getSender() {
        return sender;
    }

    public double getDouble() {
        try {
            return buffer.getDouble();
        } catch (BufferUnderflowException ex) {
            System.out.println("[casino-server] Buffer underflow on getDouble while handling opcode " + this.opcode);
            return 0;
        }
    }
}
