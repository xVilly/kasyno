package com.casino.Connection;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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
            int stringSize = buffer.getInt(); // Get the size of the byte array
            byte[] stringBytes = new byte[stringSize]; // Create a byte array of that size
            buffer.get(stringBytes); // Fill the byte array with bytes from the buffer
            return new String(stringBytes, StandardCharsets.UTF_8); // Construct a new String using UTF-8 charset
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

    public long getLong() {
        long result = 0;
        try {
            result = buffer.getLong();
        } catch (BufferUnderflowException ex) {
            System.out.println("[casino-server] Buffer underflow on getLong while handling opcode '"+this.opcode+"'.");
        }
        return result;
    }
}
