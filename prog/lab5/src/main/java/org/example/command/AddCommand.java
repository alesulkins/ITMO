package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;
/**
 * Класс команды "add", которая добавляет новый элемент в коллекцию.
 * Реализует интерфейс BaseCommand.
 */
public class AddCommand implements BaseCommand {

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "add"
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

    /**
     * Выполняет команду добавления нового элемента в коллекцию.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.addProduct(manager);
    }
}