package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Класс команды "HelpCommand", выводит справку по доступным командам.
 * Реализует интерфейс BaseCommand.
 */
public class HelpCommand implements BaseCommand {

    /**
     * Выполняет команду вывода справки по доступным командам.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param commandManager Принимает команды и выполняет их действия
     * @param args Аргумент команды, не используется в данной команде
     */
    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        HashMap<String, BaseCommand> commandHashMap = commandManager.getCommandHashMap();
        for (String name: commandHashMap.keySet()){
            BaseCommand command = commandHashMap.get(name);
            System.out.println(command.getName() +" - " + command.getDescription());
        }
    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "HelpCommand"
     */
    @Override
    public String getName() {
        return "help -";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "вывести справку по доступным командам"
     */
    @Override
    public String getDescription() {
        return "outputs help for available commands";
    }

}