package org.example.client;

import org.example.client.managers.ClientCommandManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private final DatagramSocket datagramSocket = new DatagramSocket();
    Scanner scanner = new Scanner(System.in);
    int port;

    public Client(String host, int port) throws IOException {
        System.out.println("Connecting to the server on the port " + port);
        this.port = port;
        datagramSocket.setSoTimeout(100000);
    }

    public void start() {
        try {
            ClientCommandManager clientCmdManager = new ClientCommandManager(this);
            while (true) {
                System.out.print("Enter the command: ");
                String input = scanner.nextLine().trim();

                String[] commandParts = input.split(" ");
                String cmdName = commandParts[0].toLowerCase();

                if (input.isEmpty()) {
                    System.out.println("Error: Command cannot be empty!");
                    System.out.println("Type 'help' to see available commands");
                    continue;
                }

                if (clientCmdManager.hasCommand(cmdName)) {
                    clientCmdManager.executeCommand(cmdName, commandParts);
                    continue;
                }

                if (cmdName.equals("update_id") && commandParts.length < 2) {
                    System.out.print("ID is required. Please enter ID: ");
                    String idInput = scanner.nextLine().trim();
                    while (idInput.isEmpty()) {
                        System.out.print("ID cannot be empty. Try again: ");
                        idInput = scanner.nextLine().trim();
                    }
                    input += " " + idInput;
                    commandParts = input.split(" "); // обновим массив, если понадобится
                }

                Request request = new Request(input);

                if (cmdName.equals("update_id")) {
                    if (commandParts.length < 2) {
                        System.out.println("ID is required for update_id. Usage: update_id <id>");
                        continue;
                    }
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
        Response response = (Response) oi.readObject(); // Читаем объект один раз

        return response; // прочитанный объект
    }

    private boolean isProductCommand(String cmdName) {
        return cmdName.equals("add") ||
                cmdName.equals("add_if_min");
    }
}