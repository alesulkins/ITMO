package org.example.client.managers;

import org.example.client.Client;
import org.example.client.commands.ClientCommand;
import org.example.client.commands.ExecuteScriptCommand;
import org.example.client.commands.ExitCommand;
import org.example.client.commands.HelpCommand;
import java.util.*;

public class ClientCommandManager {
    private final LinkedHashMap<String, ClientCommand> clientCommandMap = new LinkedHashMap<>();
    private final Queue<String> commandHistory;
    private Client client;
    private final String login;
    private final String password;

    public ClientCommandManager(Client client, String login, String password){
        this.client = client;
        this.login = login;
        this.password = password;
        clientCommandMap.put("help", new HelpCommand(this));
        clientCommandMap.put("exit", new ExitCommand());
        clientCommandMap.put("execute_script", new ExecuteScriptCommand(client));
        commandHistory = new LinkedList<>();
    }

    public LinkedHashMap<String, ClientCommand> getCommandHashMap() {
        return clientCommandMap;
    }

    public ClientCommand getClientCommand(String name){
        return clientCommandMap.get(name);
    }

    public boolean hasCommand(String name) {
        return clientCommandMap.containsKey(name);
    }

    // Новый метод: выполнение команды
    public void executeCommand(String name, String[] args) {
        ClientCommand command = clientCommandMap.get(name);
        if (command != null) {
            System.out.println(Arrays.toString(args));
            try {
                command.execute(args);
            } catch (Exception e) {
                System.err.println("Error executing command: " + e.getMessage());
            }
        }
    }

    public Client getClient() {
        return client;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}