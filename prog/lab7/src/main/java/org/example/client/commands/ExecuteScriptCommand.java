package org.example.client.commands;
import org.example.client.Client;
import org.example.server.managers.CollectionManager;
import org.example.server.managers.ServerCommandManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.product.*;
import org.example.shared.product.Product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ExecuteScriptCommand extends ClientCommand {
    private static Stack<String> stack = new Stack<>();
    private static ArrayList<String> productCommand = new ArrayList<>();
    private static ArrayList<String> idCommands = new ArrayList<>();
    private Client client;
    public ServerCommandManager commandManager;
    public CollectionManager manager;

    public ExecuteScriptCommand(Client client) {
        this.client=client;
    }

    static {
        productCommand.add("add");
        productCommand.add("add_if_min");
        idCommands.add("remove_greater");
        idCommands.add("remove_by_id");
    }
    @Override
    public boolean execute(String[] args) throws  IOException {
        if (args.length < 2) {
            System.out.println("Error: Missing script path. Usage: execute_script <file_path>");
            return false;
        }
        String path = args[1];
        if (stack.contains(path)) {
            System.out.println("Recursion detected: " + path);
            return false;
        }
        stack.push(path);

        File file = new File(path);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            System.out.println("Error: Invalid script file: " + path);
            stack.pop();
            return false;
        }
        System.out.println("Executing script: " + path);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(path))) {
            System.out.println("Beginning to read script...");
            String line;
            while ((line = fileReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] arg = line.split(" ");
                String command = arg[0];
                String argument = arg.length > 1 ? arg[1] : "";

                System.out.println("Executing: " + line);

                if (command.equals("execute_script")) {
                    execute(new String[]{command, argument});

                } else if (productCommand.contains(command)) {
                    Product product = readProductFromScript(fileReader);
                    Request request = new Request(command);
                    request.setProduct(product);
                    Response response = client.sendRequest(request);
                    System.out.println(response.getResponse());
                } else if (idCommands.contains(command)) {
                    String idValue = fileReader.readLine().trim(); // ID
                    Request request = new Request(command);
                    //request.setParam("arg", idValue);
                    Response response = client.sendRequest(request);
                    System.out.println(response.getResponse());
                } else if (command.equals("update_id")) {
                    String idValue = fileReader.readLine().trim();
                    Product product = readProductFromScript(fileReader);
                    Request request = new Request(command);
                    //request.setParam("arg", idValue);
                    request.setProduct(product);
                    Response response = client.sendRequest(request);
                    System.out.println(response.getResponse());
                } else {
                    Request request = new Request(line);
                    Response response = client.sendRequest(request);
                    System.out.println(response.getResponse());
                }
            }
        } catch (Exception e) {
            System.out.println("Script execution failed: " + e.getMessage());
        } finally {
            stack.pop();
        }
        return false;
    }

    private Product readProductFromScript(BufferedReader reader) throws IOException {
        try {
            String name = reader.readLine();
            if (name == null || name.trim().isEmpty()) {
                throw new IOException("Product name cannot be empty");
            }

            int x = Integer.parseInt(reader.readLine());
            long y = Long.parseLong(reader.readLine());
            if (y > 696) {
                throw new IOException("Y coordinate must be <= 696");
            }
            Coordinates coordinates = new Coordinates(x, y);

            double price = Double.parseDouble(reader.readLine());
            if (price <= 0) {
                throw new IOException("Price must be greater than 0");
            }

            UnitOfMeasure unit = UnitOfMeasure.valueOf(reader.readLine().trim().toUpperCase());

            Person owner = null;
            String hasOwner = reader.readLine();
            if (hasOwner != null && hasOwner.equalsIgnoreCase("yes")) {
                String ownerName = reader.readLine();
                if (ownerName == null || ownerName.trim().isEmpty()) {
                    throw new IOException("Owner name cannot be empty");
                }

                Integer height = null;
                String heightInput = reader.readLine();
                if (heightInput != null && !heightInput.trim().isEmpty()) {
                    height = Integer.parseInt(heightInput.trim());
                    if (height <= 0) {
                        throw new IOException("Height must be greater than 0");
                    }
                }

                Color hairColor = null;
                String colorInput = reader.readLine();
                if (colorInput != null && !colorInput.trim().isEmpty()) {
                    hairColor = Color.valueOf(colorInput.trim().toUpperCase());
                }

                Country nationality = Country.valueOf(reader.readLine().trim().toUpperCase());

                owner = new Person(ownerName.trim(), height, hairColor, nationality);
            }

            // Возвращаем созданный продукт
            return new Product(name.trim(), coordinates, price, unit, owner);

        } catch (NumberFormatException e) {
            throw new IOException("Invalid number format", e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid enum value", e);
        } catch (NullPointerException e) {
            throw new IOException("Unexpected end of file while reading product data", e);
        }
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "execute commands from specified script file";
    }
}