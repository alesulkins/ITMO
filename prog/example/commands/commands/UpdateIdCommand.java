package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.commands.ProductManager;
import org.example.manager.CollectionManager;
import org.example.product.Product;
import org.example.system.exception.ProductNotFoundException;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;
import java.util.Scanner;

/**
 * Класс команды "update id", обновляет значение элемента коллекции, id которого равен заданному.
 * Реализует интерфейс BaseCommand.
 */
public class UpdateIdCommand implements BaseCommand {
    ProductManager productManager = new ProductManager();

    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the ID of the item to update:");

        Long id = null;
        while (id == null) {
            try {
                id = Long.parseLong(scanner.nextLine());
                if (id <= 0) {
                    System.out.println("ID should be more than 0. Try again:");
                    id = null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input. Enter the number:");
            }
        }
        Product updatedProduct = productManager.createProduct();
        boolean productFound = false;
        for (Product product : manager.getCollection()) {
            if (product.getId().equals(id)) {
                productFound = true;
                updatedProduct.setId(id); // старый id
                manager.removeProduct(product);
                manager.addProduct(updatedProduct);
            }
        }
        if (!productFound) {
            throw new ProductNotFoundException(id);
        }

    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "update id"
     */
    @Override
    public String getName() {
        return "update_id";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "обновить значение элемента коллекции, id которого равен заданному"
     */
    @Override
    public String getDescription() {
        return "updates the value of a collection item whose id is equal to the specified one";
    }
}