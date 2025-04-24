package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.commands.ProductManager;
import org.example.manager.CollectionManager;
import org.example.product.Product;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;
import java.util.Optional;

/**
 * Класс команды "add_if_min", которая добавляет новый элемент в коллекцию,
 * если его значение меньше, чем у наименьшего элемента коллекции.
 * Реализует интерфейс BaseCommand.
 */
public class AddIfMinCommand implements BaseCommand {
    ProductManager productManager = new ProductManager();
    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        Optional<Product> minProduct = manager.getCollection().stream()
                .min(Product::compareTo);

        System.out.println("Creating new product for comparison...");
        Product newProduct = productManager.createProduct(); // создаем

        if (minProduct.isEmpty() || newProduct.compareTo(minProduct.get()) < 0) {
            manager.addProduct(newProduct); // <-- направляем
            System.out.println("Product added successfully (it's the new minimum)");

            System.out.println("Added product details:");
            System.out.println(newProduct);
        } else {
            System.out.println("Product not added - it's not smaller than minimum");
            System.out.println("Current minimum product:");
            System.out.println(minProduct.get());
        }
    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "add_if_min"
     */
    @Override
    public String getName() {
        return "add_if_min";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "добавляет новый элемент в коллекцию, если его значение гипотенузы меньше, чем у наименьшего элемента этой коллекции"
     */
    @Override
    public String getDescription() {
        return "adds a new item to the collection if its hypo value is less than that of the smallest item in that collection.";
    }
}