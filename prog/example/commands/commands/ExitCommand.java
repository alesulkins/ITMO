package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;

/**
 * Класс команды "exit", завершает программу без сохранения в файл.
 * Реализует интерфейс BaseCommand.
 */
public class ExitCommand implements BaseCommand {
    /**
     * Выполняет команду завершения программы без сохранения в файл.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param commandManager Принимает команды и выполняет их действия
     * @param args Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        System.exit(1);
    }

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

}