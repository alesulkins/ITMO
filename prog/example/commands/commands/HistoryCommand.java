package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;
import java.util.Queue;

/**
 * Класс команды "history", выводит последние 7 использованных команд без их аргументов.
 * Реализует интерфейс BaseCommand.
 */
public class HistoryCommand implements BaseCommand {
    /**
     * Выполняет команду вывода истории последних 7 команд.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     */
    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        Queue<String> history = commandManager.getCommandHistory();

        if (history.isEmpty()) {
            System.out.println("No commands in history yet");
            return;
        }

        System.out.println("Last 7 commands:");
        int counter = 1;
        for (String command : history) {
            //System.out.printf("%d. %s%n", counter++, command);
            System.out.println(command);
        }
    }

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

}