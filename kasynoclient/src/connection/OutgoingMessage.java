package connection;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class OutgoingMessage {
    private byte opcode;
    private ByteBuffer buffer;

    public OutgoingMessage(byte opcode) {
        this.opcode = opcode;
        this.buffer = ByteBuffer.allocate(1024);

        buffer.put(opcode);
    }

    public void putInt(int value) {
        try {
            buffer.putInt(value);
        } catch (BufferOverflowException ex) {
            System.out.println("[casino-client] Buffer overflow on putInt while handling opcode '"+this.opcode+"'.");
        }
    }

    public void putString(String text) {
        try {
            buffer.putInt(text.length());
            buffer.put(text.getBytes());
        }
        catch (BufferOverflowException ex) {
            System.out.println("[casino-client] Buffer overflow on putString while handling opcode '"+this.opcode+"'.");
        }
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
