package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Класс команды "info", выводит информацию о коллекции.
 * Реализует интерфейс BaseCommand.
 */
public class InfoCommand implements BaseCommand {

    /**
     * Выполняет команду вывода информации о коллекции, включая текущее время и количество элементов.
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
        System.out.println("Type of collection: " + manager.getCollection().getClass().getName());
        System.out.println("Time: " + LocalDateTime.now());
        System.out.println("Amount of products: " + manager.getCollection().size());
    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "info"
     */
    @Override
    public String getName() {
        return "info";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "вывести в стандартный поток вывода информацию о коллекции"
     */
    @Override
    public String getDescription() {
        return "output information about the collection to the standard output stream";
    }

}