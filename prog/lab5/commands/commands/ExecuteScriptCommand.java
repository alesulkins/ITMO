package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.product.Coordinates;
import org.example.product.Product;
import org.example.product.UnitOfMeasure;
import org.example.product.Person;
import org.example.product.Color;
import org.example.product.Country;
import org.example.product.comparator.DistanceComparator;
import org.example.system.exception.WrongArgumentException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Класс команды "execute_script", которая выполняет скрипт команд из указанного файла.
 * Реализует интерфейс BaseCommand.
 */
public class ExecuteScriptCommand implements BaseCommand {
    private static Stack<String> scriptStack = new Stack<>();
    private static ArrayList<String> complexCommands = new ArrayList<>();

    static {
        complexCommands.add("add");
        complexCommands.add("add_if_min");
        complexCommands.add("update_id");
        complexCommands.add("remove_by_id");
        complexCommands.add("remove_greater");
    }

    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager)
            throws WrongArgumentException, IOException {

        if (args.length == 0) {
            throw new WrongArgumentException("Script file path required");
        }

        String path = args[0];

        // Проверка рекурсии
        if (scriptStack.contains(path)) {
            System.out.println("Recursion detected in script: " + path);
            return;
        }

        scriptStack.push(path);

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(" ");
                String commandName = parts[0];
                String[] commandArgs = parts.length > 1 ?
                        Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

                System.out.println("Executing: " + line);

                if (commandName.equals("execute_script")) {
                    System.out.println("Executing nested script...");
                    commandManager.execute(line);
                    continue;
                }

                if (complexCommands.contains(commandName)) {
                    processComplexCommand(reader, commandName, manager, commandManager);
                } else {
                    commandManager.execute(line);
                }
            }
        } finally {
            scriptStack.pop();
        }
    }

    private void processComplexCommand(BufferedReader reader, String commandName,
                                       CollectionManager manager, CommandManager commandManager)
            throws IOException, WrongArgumentException {

        Product product = readProductFromScript(reader);

        switch (commandName) {
            case "add":
                manager.getCollection().add(product);
                System.out.println("Product added from script");
                break;

            case "add_if_min":
                Optional<Product> minProduct = manager.getCollection().stream()
                        .min(Product::compareTo);

                if (minProduct.isEmpty() || product.compareTo(minProduct.get()) < 0) {
                    manager.getCollection().add(product);
                    System.out.println("Product added as new minimum");
                } else {
                    System.out.println("Product not added - not smaller than current minimum");
                }
                break;

            case "update_id":
                if (product.getId() == null) {
                    throw new WrongArgumentException("ID must be specified for update");
                }

                boolean updated = false;
                for (Product p : manager.getCollection()) {
                    if (p.getId().equals(product.getId())) {
                        manager.getCollection().remove(p);
                        manager.getCollection().add(product);
                        updated = true;
                        break;
                    }
                }

                if (!updated) {
                    System.out.println("Product with ID " + product.getId() + " not found");
                }
                break;

            case "remove_by_id":
                String idLine = reader.readLine();
                if (idLine == null) throw new IOException("Missing ID for remove_by_id");
                long id = Long.parseLong(idLine.trim());

                boolean removed = manager.getCollection().removeIf(p -> p.getId().equals(id));
                System.out.println(removed ? "Product removed" : "Product not found");
                break;

            case "remove_greater":
                DistanceComparator comparator = new DistanceComparator(product);
                List<Product> toRemove = manager.getCollection().stream()
                        .filter(p -> comparator.compare(p, product) > 0)
                        .toList();

                manager.getCollection().removeAll(toRemove);
                System.out.println("Removed " + toRemove.size() + " elements");
                break;
        }
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

            // Создание продукта с использованием правильного конструктора
            return new Product(name.trim(), coordinates, price, unit, owner);

        } catch (NumberFormatException e) {
            throw new IOException("Invalid number format", e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid enum value", e);
        } catch (NullPointerException e) {
            throw new IOException("Unexpected end of file while reading product data");
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