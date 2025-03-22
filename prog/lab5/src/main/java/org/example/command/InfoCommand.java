package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

import java.time.LocalDateTime;

/**
 * Класс команды "info", выводит информацию о коллекции.
 * Реализует интерфейс BaseCommand.
 */
public class InfoCommand implements BaseCommand {

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

    /**
     * Выполняет команду вывода информации о коллекции, включая текущее время и количество элементов.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды (информация о коллекции)
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        System.out.println("Time: " + LocalDateTime.now());
        System.out.println("Amount of products: " + manager.getCollection().size());
        return "";
    }
}