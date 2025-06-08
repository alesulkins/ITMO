package org.example.client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar client.jar <port>");
            return;
        }

        try {
            int port = Integer.parseInt(args[0]);
            Client client = new Client("localhost", port);
            client.start();
        } catch (NumberFormatException e) {
            System.out.println("Port must be a number.");
        } catch (IOException e) {
            System.out.println("IO error occurred: " + e.getMessage());
        }
    }
}
