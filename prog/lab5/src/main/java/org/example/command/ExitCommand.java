package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "exit", завершает программу без сохранения в файл.
 * Реализует интерфейс BaseCommand.
 */
public class ExitCommand implements BaseCommand {

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "exit"
     */
    @Override
    public String getName() {
        return "exit";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "завершение программы без сохранения в файл"
     */
    @Override
    public String getDescription() {
        return "program termination without saving to a file";
    }

    /**
     * Выполняет команду завершения программы без сохранения в файл.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.exit();
    }
}