package org.example.server.commands;

import org.example.server.managers.CollectionManager;
import org.example.server.managers.ServerCommandManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import java.util.Deque;

public class HistoryCommand extends ServerCommand {
    private final ServerCommandManager commandManager;

    public HistoryCommand(CollectionManager cm, ServerCommandManager scm) {
        super("history", "shows last 7 executed commands", cm);
        this.commandManager = scm;
    }

    @Override
    public Response execute(Request request) {
        Deque<String> history = commandManager.getCommandHistory();
        if (history.isEmpty()) {
            return new Response("Command history is empty");
        }

        StringBuilder sb = new StringBuilder("Last commands:\n");
        history.forEach(cmd -> sb.append("- ").append(cmd).append("\n"));
        return new Response(sb.toString());
    }
}