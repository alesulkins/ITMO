package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "add_if_min", которая добавляет новый элемент в коллекцию,
 * если его значение меньше, чем у наименьшего элемента коллекции.
 * Реализует интерфейс BaseCommand.
 */
public class AddIfMinCommand implements BaseCommand {

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
     * @return Описание команды "добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции"
     */
    @Override
    public String getDescription() {
        return "adds a new item to the collection if its value is less than that of the smallest item in that collection.";
    }

    /**
     * Выполняет команду добавления нового элемента в коллекцию, если его значение меньше минимального.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.addIfMin(manager);
    }
}