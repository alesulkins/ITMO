package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "remove_by_id", удаляет элемент из коллекции по его id.
 * Реализует интерфейс BaseCommand.
 */
public class RemoveByIdCommand implements BaseCommand {

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

    /**
     * Выполняет команду удаления элемента из коллекции по заданному id.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды - id элемента, который нужно удалить
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        Long id = Long.parseLong(arg);
        return receiver.removeByIdProducts(manager, id);
    }
}