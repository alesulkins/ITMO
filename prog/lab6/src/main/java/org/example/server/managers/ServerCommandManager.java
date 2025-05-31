package org.example.server.managers;

import org.example.server.commands.*;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;

import java.util.*;

public class ServerCommandManager {
    private final Deque<String> commandHistory = new ArrayDeque<>(7);
    private final LinkedHashMap<String, ServerCommand> serverCommandMap = new LinkedHashMap<>();
    private final  CollectionManager cm;

    public ServerCommandManager(CollectionManager cm) {
        this.cm = cm;
        serverCommandMap.put("add", new AddCommand(cm));
        serverCommandMap.put("add_if_min", new AddIfMinCommand(cm));
        serverCommandMap.put("clear", new ClearCommand(cm));
        serverCommandMap.put("info", new InfoCommand(cm));
        serverCommandMap.put("remove_by_id", new RemoveByIdCommand(cm));
        serverCommandMap.put("remove_greater", new RemoveGreater(cm));
        serverCommandMap.put("show", new ShowCommand(cm));
        serverCommandMap.put("group_counting_by_id", new GroupCountingByIdCommand(cm));
        serverCommandMap.put("help", new ServerHelpCommand(this));
        serverCommandMap.put("print_ascending", new PrintAscendingCommand(cm));
        serverCommandMap.put("print_ascending_owner", new PrintAscendingFieldOwnerCommand(cm));
        serverCommandMap.put("history", new HistoryCommand(cm, this));
        serverCommandMap.put("update_id", new UpdateIdCommand(cm));

    }

    public LinkedHashMap<String, ServerCommand> getCommandHashMap() {
        return serverCommandMap;
    }


    public Deque<String> getCommandHistory() {
        return commandHistory;
    }

    public ServerCommand getServerCommand(String name) {
        if (!name.equalsIgnoreCase("history")) {
            commandHistory.addLast(name);
            if (commandHistory.size() > 7) {
                commandHistory.removeFirst();
            }
        }

        if (name.equalsIgnoreCase("save")) {
            return new ServerCommand("save", "blocked command", cm) {
                @Override
                public Response execute(Request request) {
                    return new Response("You don't have enough rights to use this command.");
                }
            };
        }

        ServerCommand command = serverCommandMap.get(name);
        if (command == null) {
            return new ServerCommand(name, "unknown command", cm) {
                @Override
                public Response execute(Request request) {
                    return new Response("Unknown command: '" + name + "'. Type 'help' to see available commands.");
                }
            };
        }
        return serverCommandMap.get(name);
    }
}