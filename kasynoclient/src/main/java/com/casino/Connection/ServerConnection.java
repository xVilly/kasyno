package com.casino.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerConnection {
    private String serverAddress;
    private int serverPort;

    private Socket socket;
    private MessageParse msgParser;
    private MessageSend msgSender;

    private OutputStream outputStream;
    private InputStream inputStream;
    
    public ServerConnection(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.msgParser = new MessageParse();
        this.msgSender = new MessageSend(this);
    }

    public void Start() {
        try {
            socket = new Socket(serverAddress, serverPort);
        } catch (Exception ex) {
            System.out.println("[casino-client] Failed to connect to server '" + serverAddress + ":" + serverPort + "'");
            return;
        }

        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            // Start a separate thread to read data from the server
            Thread serverReaderThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        onReceive(buffer, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }); 
            serverReaderThread.start();

            // Thread for keeping the connection alive
            Thread keepAliveThread = new Thread(() -> {
                try {
                    while (!socket.isClosed()) {
                        sendPacket(new byte[] { 0x00 });
                        Thread.sleep(50000);
                    }
                } catch (InterruptedException e) {
                    System.out.println("[casino-client] KeepAlive thread terminated");     
                }
            }); 
            keepAliveThread.start();
        } catch (Exception ex) {
            System.out.println("[casino-client] Failed to create input/output streams for server connection.");
        }
    }

    private void onReceive(byte[] data, int size) {
        System.out.println("[casino-client] Received " + size + " bytes from server '" + socket.getInetAddress().getHostAddress() + "'");
        msgParser.parseMessage(data, size);
    }

    public void sendPacket(byte[] data) {
        if (socket.isClosed()) {
            System.out.println("[casino-client] Connection is already closed. Cannot send packets.");
            return;
        }
        try {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException ex) {
            System.out.println("[casino-client] Failed to send packet to server.");
        }
    }

    public MessageSend getMessageSender() {
        return msgSender;
    }
    
}