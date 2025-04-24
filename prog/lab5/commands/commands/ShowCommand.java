package org.example.commands.commands;


import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.product.Product;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;

/**
 * Класс команды "show", выводит в стандартный поток вывода все элементы коллекции в строковом представлении.
 * Реализует интерфейс BaseCommand.
 */
public class ShowCommand implements BaseCommand {

    /**
     * Выполняет команду вывода всех элементов коллекции в строковом представлении.
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
        for (Product product: manager.getCollection()){
            System.out.println(product);
        }
        if (manager.getCollection().isEmpty()){
            System.out.println("no products in the collection yet!");
        }
    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "show"
     */
    @Override
    public String getName() {
        return "show";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "вывести в стандартный поток вывода все элементы коллекции в строковом представлении"
     */
    @Override
    public String getDescription() {
        return "output all the elements of the collection in a string representation to the standard output stream";
    }

}