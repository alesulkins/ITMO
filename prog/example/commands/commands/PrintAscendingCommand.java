package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.product.Product;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;
import java.util.Stack;

/**
 * Класс команды "print_ascending", выводит элементы коллекции в порядке возрастания.
 * Реализует интерфейс BaseCommand.
 */
public class PrintAscendingCommand implements BaseCommand {

    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        if (args.length != 0) {
            throw new WrongArgumentException("This command doesn't accept arguments");
        }

        Stack<Product> collection = manager.getCollection();

        if (collection.isEmpty()) {
            System.out.println("Collection is empty");
            return;
        }

        System.out.println("Elements by their distance from (0;0) coordinate in ascending order:");
        collection.stream()
                .sorted()
                .forEach(product -> System.out.println(product));
    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "print_ascending"
     */
    @Override
    public String getName() {
        return "print_ascending";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "вывести элементы коллекции в порядке возрастания"
     */
    @Override
    public String getDescription() {
        return "displays the collection items in ascending order";
    }

    /**
     * Выполняет команду вывода элементов коллекции в порядке возрастания.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
}