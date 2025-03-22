package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "HelpCommand", выводит справку по доступным командам.
 * Реализует интерфейс BaseCommand.
 */
public class HelpCommand implements BaseCommand {

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "HelpCommand"
     */
    @Override
    public String getName() {
        return "HelpCommand";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "вывести справку по доступным командам"
     */
    @Override
    public String getDescription() {
        return "output help for available commands";
    }

    /**
     * Выполняет команду вывода справки по доступным командам.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.help();
    }
}