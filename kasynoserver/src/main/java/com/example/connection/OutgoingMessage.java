package com.example.connection;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class OutgoingMessage {
    private byte opcode;
    private ByteBuffer buffer;
    private ClientHandler sender;

    public OutgoingMessage(byte opcode, ClientHandler sender) {
        this.opcode = opcode;
        this.buffer = ByteBuffer.allocate(1024);
        this.sender = sender;

        buffer.put(opcode);
    }

    public void putInt(int value) {
        try {
            buffer.putInt(value);
        } catch (BufferOverflowException ex) {
            System.out.println("[casino-server] Buffer overflow on putInt while handling opcode '"+this.opcode+"'.");
        }
    }

    public void putString(String text) {
        try {
            buffer.putInt(text.length());
            buffer.put(text.getBytes());
        }
        catch (BufferOverflowException ex) {
            System.out.println("[casino-server] Buffer overflow on putString while handling opcode '"+this.opcode+"'.");
        }
    }

    public void putDouble(double value) {
        try {
            buffer.putDouble(value);
        } catch (BufferOverflowException ex) {
            System.out.println("[casino-server] Buffer overflow on putDouble while handling opcode '"+this.opcode+"'.");
        }
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public ClientHandler getSender() {
        return sender;
    }

    public void putLong(long date) {
        try {
            buffer.putLong(date);
        } catch (BufferOverflowException ex) {
            System.out.println("[casino-server] Buffer overflow on putLong while handling opcode '"+this.opcode+"'.");
        }
    }
}
