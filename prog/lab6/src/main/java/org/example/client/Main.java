package org.example.client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Client client =new Client("localhost", Integer.parseInt(args[0]));
            client.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
