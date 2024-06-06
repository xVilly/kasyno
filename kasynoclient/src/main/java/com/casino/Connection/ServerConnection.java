package com.casino.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import com.casino.Controllers.ConnectionController;
import com.casino.Controllers.SceneManager;

import javafx.application.Platform;

public class ServerConnection {
    private String serverAddress;
    private int serverPort;

    private Socket socket;
    private MessageParse msgParser;
    private MessageSend msgSender;

    private OutputStream outputStream;
    private InputStream inputStream;

    private Date lastPingSent = null;
    
    public ServerConnection(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.msgParser = new MessageParse();
        this.msgSender = new MessageSend(this);
    }

    public void Start() {
        ConnectionController controller = (ConnectionController)SceneManager.getInstance().getController("ConnectWindow");
        try {
            socket = new Socket(serverAddress, serverPort);
            System.out.println("[casino-client] Connected to server '" + serverAddress + ":" + serverPort + "'");
            Platform.runLater(() -> controller.onConnect());
        } catch (Exception ex) {
            System.out.println("[casino-client] Failed to connect to server '" + serverAddress + ":" + serverPort + "'");
            Platform.runLater(() -> controller.onFailedConnect());
            return;
        }

        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            // Start a separate thread to read data from the server
            Thread serverReaderThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        onReceive(buffer, bytesRead);
                    }
                } catch (IOException e) {
                    System.out.println("[casino-client] Connection to server '" + serverAddress + "' was closed.");
                    onDisconnect();
                }
            }); 
            serverReaderThread.start();

            // Thread for keeping the connection alive
            Thread keepAliveThread = new Thread(() -> {
                try {
                    while (!(socket == null || socket.isClosed())) {
                        sendPacket(new byte[] { 0x00 });
                        lastPingSent = new Date();
                        Thread.sleep(10000);
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
        if (socket == null || socket.isClosed()) {
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

    public void registerCallback(byte opcode, ClientActionCallback callback) {
        msgParser.setCallback(opcode, callback);
    }

    public int getPing() {
        if (lastPingSent == null) {
            return -1;
        }
        return (int) (new Date().getTime() - lastPingSent.getTime());
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException ex) {
            System.out.println("[casino-client] Failed to close the connection to the server.");
        }
    }

    public void onDisconnect() {
        ConnectionController controller = (ConnectionController)SceneManager.getInstance().getController("ConnectWindow");
        Platform.runLater(() -> controller.onDisconnect());
    }
}