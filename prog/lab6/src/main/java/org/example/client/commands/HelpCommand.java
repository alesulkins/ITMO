package org.example.client.commands;

import org.example.client.Client;
import org.example.client.managers.ClientCommandManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;

import java.util.Map;
import java.util.TreeMap;

public class HelpCommand extends ClientCommand {
    private final ClientCommandManager clientCommandManager;

    public HelpCommand(ClientCommandManager clientCommandManager) {
        this.clientCommandManager = clientCommandManager;
    }

    @Override
    public boolean execute(String[] args) {
        Map<String, String> allCommands = new TreeMap<>();

        // Собираем клиентские команды
        clientCommandManager.getCommandHashMap().forEach((name, cmd) -> {
            if (cmd.getName() != null && cmd.getDescription() != null) {
                allCommands.put(name, cmd.getDescription());
            }
        });

        // Собираем серверные команды
        try {
            Client client = clientCommandManager.getClient();
            Response response = client.sendRequest(new Request("help"));
            if (response != null && response.getResponse() != null && !response.getResponse().isEmpty()) {
                String[] lines = response.getResponse().split("\n");
                for (String line : lines) {
                    String[] parts = line.split(" - ", 2);
                    if (parts.length == 2) {
                        allCommands.put(parts[0], parts[1]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении серверных команд: " + e.getMessage());
        }

        // Выводим все команды в алфавитном порядке
        if (allCommands.isEmpty()) {
            System.out.println("Нет доступных команд.");
            return false;
        }

        System.out.println("=== Доступные команды ===");
        allCommands.forEach((name, description) ->
                System.out.printf("%-25s - %s%n", name, description)
        );

        return true;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "показать все доступные клиентские и серверные команды";
    }
}