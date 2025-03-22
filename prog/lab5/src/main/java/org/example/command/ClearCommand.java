package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "clear", которая очищает коллекцию.
 * Реализует интерфейс BaseCommand.
 */
public class ClearCommand implements BaseCommand {

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "clear"
     */
    @Override
    public String getName() {
        return "clear";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "очищает коллекцию"
     */
    @Override
    public String getDescription() {
        return "clears the collection";
    }

    /**
     * Выполняет команду очистки коллекции.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.clear(manager);
    }
}