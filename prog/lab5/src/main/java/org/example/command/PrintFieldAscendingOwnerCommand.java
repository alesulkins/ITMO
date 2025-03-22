package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "print_field_ascending_owner", выводит значения поля owner всех элементов коллекции в порядке возрастания.
 * Реализует интерфейс BaseCommand.
 */
public class PrintFieldAscendingOwnerCommand implements BaseCommand {

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "print_field_ascending_owner"
     */
    @Override
    public String getName() {
        return "print_field_ascending_owner";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "вывести значения поля owner всех элементов в порядке возрастания"
     */
    @Override
    public String getDescription() {
        return "print the values of the owner field of all elements in ascending order";
    }

    /**
     * Выполняет команду вывода значений поля owner всех элементов в порядке возрастания.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.printFieldAscendingOwner(manager);
    }
}