package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "print_ascending", выводит элементы коллекции в порядке возрастания.
 * Реализует интерфейс BaseCommand.
 */
public class PrintAscendingCommand implements BaseCommand {

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
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.printAscending(manager);
    }
}