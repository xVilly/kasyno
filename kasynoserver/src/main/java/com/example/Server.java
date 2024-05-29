package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import com.example.connection.ClientHandler; 

public class Server {
    public static void main(String[] args) {
        Server server = new Server();
        server.start(9999);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
        
                // Create a new thread to handle each client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread((Runnable)clientHandler);
                clientHandler.runningThread = thread;
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}