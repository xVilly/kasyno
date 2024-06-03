package com.example.connection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

public class ClientHandler implements Runnable {
    private UUID connectionId;
    private Socket clientSocket;
    private OutputStream outputStream;
    private InputStream inputStream;

    public Thread runningThread = null;

    private boolean failDuringInitialization = false;
    private String initializationError = "";

    private String associatedUser = null;

    public String getAssociatedUser() {
        return associatedUser;
    }

    public void setAssociatedUser(String associatedUser) {
        this.associatedUser = associatedUser;
    }

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.connectionId = UUID.randomUUID();
        
        
        try {
            outputStream = clientSocket.getOutputStream();
            inputStream = clientSocket.getInputStream();
            clientSocket.setSoTimeout(60000);
            clientSocket.setKeepAlive(true);
        } catch (IOException e) {
            initializationError = e.getMessage();
            try {
                clientSocket.close();
            } catch (Exception ex) {}
        }

        if (clientSocket.isConnected()) {
            onConnect();
        } else {
            failDuringInitialization = true;
            System.out.println("[casino-server] Client '"+clientSocket.getInetAddress().getHostAddress()+"' failed to connect (id "+connectionId+"): "+ initializationError);
        }
    }
    
    public void listenForPackets() throws IOException, InterruptedException
    {
        byte[] receivedData = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(receivedData)) != -1) {
            // Process received data
            onReceive(receivedData, bytesRead);
        }
    }

    public void onConnect() {
        ConnectionManager.getInstance().registerClientConnection(this);
    }

    public void onDisconnect() {
        ConnectionManager.getInstance().unregisterClientConnection(this);
    }

    public void onReceive(byte[] data, int size) {
        // skip heartbeat packets
        if (!(size == 1 && data[0] == 0x0)) {
            System.out.println("[casino-server] Received " + size + " bytes from client '" + clientSocket.getInetAddress().getHostAddress() + "' (id " + connectionId + ")");
        }
        ConnectionManager.getInstance().parseClientMessage(data, size, this);
    }

    public void sendPacket(byte[] data) {
        if (clientSocket.isClosed()) {
            System.out.println("[casino-server] Connection (id "+connectionId+") is already closed. Cannot send packets.");
            return;
        }
        try {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("[casino-server] Error during packet send (to "+clientSocket.getInetAddress().getHostAddress()+"): " + e.getMessage());
        }
    }

    public void disconnect() {
        runningThread.interrupt();
    }

    public String getIpAddress() {
        return clientSocket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        try {
            listenForPackets();
        } catch (InterruptedException interruptEx) {
            System.out.println("[casino-server] ClientHandler thread interrupted");
        } catch (SocketTimeoutException timeoutEx) {
            System.out.println("[casino-server] Client '"+clientSocket.getInetAddress().getHostAddress()+"' (id "+connectionId+") timed out");
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (Exception ex) {}
            onDisconnect();
        }
    }

    public UUID getConnectionId() {
        return connectionId;
    }
}