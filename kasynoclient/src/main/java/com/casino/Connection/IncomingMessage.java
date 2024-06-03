package com.casino.Connection;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class IncomingMessage {
    private byte opcode;
    private ByteBuffer buffer;
    private int size;

    public IncomingMessage(byte opcode, ByteBuffer buffer, int size) {
        this.opcode = opcode;
        this.buffer = buffer;
        this.size = size;
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

    public double getDouble() {
        double result = 0;
        try {
            result = buffer.getDouble();
        } catch (BufferUnderflowException ex) {
            System.out.println("[casino-server] Buffer underflow on getDouble while handling opcode '"+this.opcode+"'.");
        }
        return result;
    
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
}
