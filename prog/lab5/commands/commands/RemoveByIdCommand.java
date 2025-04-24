package org.example.commands.commands;


import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.product.Product;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;
import java.util.Scanner;

/**
 * Класс команды "remove_by_id", удаляет элемент из коллекции по его id.
 * Реализует интерфейс BaseCommand.
 */
public class RemoveByIdCommand implements BaseCommand {

    /**
     * Выполняет команду удаления элемента из коллекции по id.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param commandManager выполняет действия команд
     * @param args Аргумент команды - id элемента, который нужно удалить
     */
    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter product ID to remove:");
        long id;
        try {
            id = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: ID must be a number");
            return;
        }

        boolean removed = false;
        for (Product product : manager.getCollection()){
            if (product.getId() == id) {
                manager.removeProduct(product);
                removed = true;
                break;
            }
        }

        if (removed) {
            System.out.println("Product with ID " + id + " was successfully removed");
        } else {
            System.out.println("Product with ID " + id + " not found");
        }
    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "remove_by_id"
     */
    @Override
    public String getName() {
        return "remove_by_id";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "удалить элемент из коллекции по его id"
     */
    @Override
    public String getDescription() {
        return "deletes an item from the collection by its id";
    }


}