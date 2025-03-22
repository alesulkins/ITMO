package org.example.manager;

import org.example.command.*;
import org.example.product.Product;
import org.example.product.UnitOfMeasure;
import org.example.util.Receiver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class CommandManager {
    private final HashMap<String, BaseCommand> commands; //зачем
    private final Queue<String> commandHistory;

    public CommandManager() {
        commands = new HashMap<>();
        commands.put("add", new AddCommand());
        commands.put("add_if_min", new AddIfMinCommand());
        commands.put("clear", new ClearCommand());
        commands.put("execute_script", new ExecuteScriptCommand());
        commands.put("exit", new ExitCommand());
        commands.put("group_counting_by_id", new GroupCountingByIdCommand());
        commands.put("help", new HelpCommand());
        commands.put("history", new HistoryCommand());
        commands.put("info", new InfoCommand());
        commands.put("print_ascending", new PrintAscendingCommand());
        commands.put("print_field_ascending_owner", new PrintFieldAscendingOwnerCommand());
        commands.put("remove_by_id", new RemoveByIdCommand());
        commands.put("remove_greater", new RemoveGreaterCommand());
        commands.put("save", new SaveCommand());
        commands.put("show", new ShowCommand());
        commands.put("update_id", new UpdateIdCommand());
        commandHistory = new LinkedList<>();

    }

    public String doCommand(String input, CollectionManager collectionManager, Receiver receiver) {
        String commandName = input.split(" ")[0];
        addToHistory(commandName);

        String arg = null;
        try{
            arg = input.split(" ")[1];
        } catch (Exception ignored){

        }
        if (commands.containsKey(commandName)) {
            return commands.get(commandName).execute(collectionManager, receiver, arg);
        } else {
            return "Unknown command: " + input;
        }
    }

    private  void addToHistory(String command) {
        if (commandHistory.size()>=7) {
            commandHistory.poll();
        }
        commandHistory.offer(command);
    }

    public  Queue<String>getCommandHistory() {
        return commandHistory;
    }

    public HashMap<String, BaseCommand> getCommands() {
        return commands;
    }
}
