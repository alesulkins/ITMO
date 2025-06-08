package org.example.server.commands;

import org.example.server.managers.ServerCommandManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import java.util.stream.Collectors;

public class ServerHelpCommand extends ServerCommand {
    private final ServerCommandManager commandManager;

    public ServerHelpCommand(ServerCommandManager scm) {
        super("help", "show server commands", null);
        this.commandManager = scm;
    }

    @Override
    public Response execute(Request request) {
        String commands = commandManager.getCommandHashMap().values().stream()
                .filter(cmd -> cmd.getName() != null && cmd.getDescription() != null)
                .map(cmd -> cmd.getName() + " - " + cmd.getDescription())
                .sorted()
                .collect(Collectors.joining("\n"));

        System.out.println("Формируем ответ сервера: [" + commands + "]"); // Отладочный вывод
        if (commands.isEmpty()) {
            return new Response("No server commands available.");
        }
        return new Response(commands);
    }
}