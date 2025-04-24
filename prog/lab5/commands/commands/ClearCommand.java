package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;

/**
 * Класс команды "clear", которая очищает коллекцию.
 * Реализует интерфейс BaseCommand.
 */
public class ClearCommand implements BaseCommand {

    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        manager.clearCollection();
        System.out.println("Collection is cleared");
    }

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
}