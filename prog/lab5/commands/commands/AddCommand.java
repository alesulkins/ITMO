package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.commands.ProductManager;
import org.example.manager.CollectionManager;
import org.example.product.Product;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;

/**
 * Класс команды "addProduct", которая добавляет новый элемент в коллекцию.
 * Реализует интерфейс BaseCommand.
 */
public class AddCommand implements BaseCommand {
    ProductManager productManager = new ProductManager();

    /**
     * Выполняет команду добавления нового элемента в коллекцию.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param commandManager Принимает команды и выполняет их действия
     * @param args Аргумент команды, не используется в данной команде
     */
    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        if (args.length != 0) {
            throw new WrongArgumentException("info");
        }
        try {
            Product product = productManager.createProduct();// <-- создаем
            manager.addProduct(product); // <-- направляем на добавление
            System.out.println("Product added");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "addProduct"
     */
    @Override
    public String getName() {
        return "add";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "добавить новый элемент в коллекцию"
     */
    @Override
    public String getDescription() {
        return "add new element into the collection";
    }

}