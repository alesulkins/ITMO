package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "execute_script", которая выполняет скрипт команд из указанного файла.
 * Реализует интерфейс BaseCommand.
 */
public class ExecuteScriptCommand implements BaseCommand {

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "execute_script"
     */
    @Override
    public String getName() {
        return "execute_script";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "считать и исполнить скрипт из указанного файла"
     */
    @Override
    public String getDescription() {
        return "reads and executes the script from the file";
    }

    /**
     * Выполняет команду чтения и исполнения скрипта из указанного файла.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Имя файла скрипта для выполнения
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.executeScript(manager, arg);
    }
}