package org.example.commands;

import org.example.commands.commands.*;
import org.example.manager.CollectionManager;

import java.util.*;

public class CommandManager {
    private final CollectionManager collectionManager = new CollectionManager();
    private final LinkedHashMap<String, BaseCommand> commands = new LinkedHashMap<>();
    public String path;//зачем
    private final Queue<String> commandHistory;

    public CommandManager() throws Exception {
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

    public void execute(String input) {
        String[] args = input.split(" ");
        String commandName = args[0];
        addToHistory(commandName);
        if (commands.containsKey(commandName)) {
            BaseCommand command = commands.get(commandName);
            args = Arrays.copyOfRange(args, 1, args.length);
            try {
                command.execute(args, collectionManager, this);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Unknown command: " + input);
        }
    }

    private  void addToHistory(String command) {
        if (commandHistory.size()>=7) {
            commandHistory.poll();
        }
        commandHistory.offer(command);
    }

    public Queue<String>getCommandHistory() {
        return commandHistory;
    }

    public LinkedHashMap<String, BaseCommand> getCommandHashMap() {
        return commands;
    }
    public HashMap<String, BaseCommand> getCommands() {
        return commands;
    }
    public CollectionManager getCollectionManager(){
        return collectionManager;
    }
}
