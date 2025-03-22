package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "save", сохраняет коллекцию в файл.
 * Реализует интерфейс BaseCommand.
 */
public class SaveCommand implements BaseCommand {

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "save"
     */
    @Override
    public String getName() {
        return "save";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "Сохраняет коллекцию в файл"
     */
    @Override
    public String getDescription() {
        return "saves collection into the file";
    }

    /**
     * Выполняет команду сохранения коллекции в файл.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды - имя файла для сохранения
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.save(manager, arg);
    }
}