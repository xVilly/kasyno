package com.example.connection;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
