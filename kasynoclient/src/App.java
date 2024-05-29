import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

import connection.ServerConnection;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter server IP address: ");
        String serverAddress = scanner.nextLine();
        System.out.print("Enter server port: ");
        int serverPort = scanner.nextInt();

        ServerConnection server = new ServerConnection(serverAddress, serverPort);
        server.Start();

        while(true) {
            System.out.print("> ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("exit")) {
                    break;
                }
                else if (line.equals("create")) {
                    System.out.println("[Account Creation]");
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Address: ");
                    String address = scanner.nextLine();
                    System.out.print("Age: ");
                    int age = scanner.nextInt();
                    server.getMessageSender().sendCreateAccount(username, password, email, address, age);
                }
                else if (line.equals("login")) {
                    System.out.println("[Account Login]");
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    server.getMessageSender().sendLogin(username, password);
                }
            }
        }
    }
}
