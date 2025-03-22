package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "history", выводит последние 7 использованных команд без их аргументов.
 * Реализует интерфейс BaseCommand.
 */
public class HistoryCommand implements BaseCommand {

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "history"
     */
    @Override
    public String getName() {
        return "history";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "выводит последние 7 использованных команд без их аргументов"
     */
    @Override
    public String getDescription() {
        return "outputs the last 7 commands used without their arguments.";
    }

    /**
     * Выполняет команду вывода истории последних 7 команд.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.history();
    }
}