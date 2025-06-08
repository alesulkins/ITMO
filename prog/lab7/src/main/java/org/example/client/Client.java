package org.example.client;

import org.example.client.managers.ClientCommandManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;

import java.io.*;
import java.net.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
    private final DatagramSocket datagramSocket = new DatagramSocket();
    private final Scanner scanner = new Scanner(System.in);
    private final int port;
    private String login;
    private String password;

    public Client(String host, int port) throws IOException {
        System.out.println("Connecting to the server on the port " + port);
        this.port = port;
        datagramSocket.setSoTimeout(30000);
    }

    public void start() {
        try {
            authenticateUser();

            ClientCommandManager clientCmdManager = new ClientCommandManager(this, login, password);

            while (true) {
                System.out.print("Enter the command: ");
                String input;
                try {
                    input = scanner.nextLine().trim();
                } catch (NoSuchElementException e) {
                    System.out.println("\nEOF detected. Exiting...");
                    System.exit(0);
                    return;
                }

                if (input.isEmpty()) {
                    System.out.println("Error: Command cannot be empty!");
                    continue;
                }

                String[] commandParts = input.split(" ");
                String cmdName = commandParts[0].toLowerCase();

                if (clientCmdManager.hasCommand(cmdName)) {
                    clientCmdManager.executeCommand(cmdName, commandParts);
                    continue;
                }

                if (cmdName.equals("update_id") && commandParts.length < 2) {
                    System.out.print("ID is required. Please enter ID: ");
                    String idInput = scanner.nextLine().trim();
                    input += " " + idInput;
                    commandParts = input.split(" ");
                }

                Request request = new Request(input, login, password);

                if (cmdName.equals("update_id")) {
                    request.setProduct(ProductManager.createProduct());
                } else if (isProductCommand(cmdName)) {
                    request.setProduct(ProductManager.createProduct());
                }

                int attempts = 3;
                boolean success = false;
                while (attempts > 0 && !success) {
                    try {
                        Response response = sendRequest(request);
                        System.out.println("Server response: " + response.getResponse());
                        success = true;
                    } catch (SocketTimeoutException e) {
                        attempts--;
                        System.out.println("Server is not responding. Attempts left: " + attempts);
                        if (attempts == 0) {
                            System.out.println("Giving up. Please check if the server is running and try again later.");
                        }
                    } catch (IOException e) {
                        System.out.println("Network error occurred: " + e.getMessage());
                        break;
                    } catch (ClassNotFoundException e) {
                        System.out.println("Invalid response from server.");
                        break;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void authenticateUser() {
        while (true) {
            try {
                System.out.print("Do you want to login or register? (login/register): ");
                String choice = scanner.nextLine().trim().toLowerCase();

                if (!choice.equals("login") && !choice.equals("register")) {
                    System.out.println("Unknown option. Try again.");
                    continue;
                }
                System.out.print("Enter login: ");
                this.login = scanner.nextLine().trim();
                System.out.print("Enter password: ");
                this.password = scanner.nextLine().trim();

                Request request = new Request(choice, login, password);
                Response response = sendRequest(request);
                System.out.println("Server response: " + response.getResponse());

                if (response.getResponse().toLowerCase().contains("success")) {
                    break;
                } else {
                    System.out.println("Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error during authentication: " + e.getMessage());
            }
        }
    }

    public Response sendRequest(Request request) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);

        InetAddress serverAddress = InetAddress.getByName("localhost");
        DatagramPacket sendPacket = new DatagramPacket(baos.toByteArray(), baos.size(), serverAddress, port);
        datagramSocket.send(sendPacket);
        return getResponse();
    }

    public Response getResponse() throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[4096];
        DatagramPacket inputPacket = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(inputPacket);

        ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(inputPacket.getData()));
        return (Response) oi.readObject();
    }

    private boolean isProductCommand(String cmdName) {
        return cmdName.equals("add") ||
                cmdName.equals("add_if_min") ||
                cmdName.equals("remove_greater");

    }
}
