package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "group_counting_by_id", группирует элементы коллекции
 * по значению поля id и выводит количество элементов в каждой группе.
 * Реализует интерфейс BaseCommand.
 */
public class GroupCountingByIdCommand implements BaseCommand {

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "group_counting_by_id"
     */
    @Override
    public String getName() {
        return "group_counting_by_id";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "сгруппировать элементы коллекции по значению поля id, вывести количество элементов в каждой группе"
     */
    @Override
    public String getDescription() {
        return "groups the collection items by the value of the id field, outputs the number of items in each group";
    }

    /**
     * Выполняет команду группировки элементов коллекции по значению поля id.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.groupCountingById(manager);
    }
}