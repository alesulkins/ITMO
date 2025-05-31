package org.example.client.commands;

import org.example.server.commands.SaveCommand;

public class ExitCommand extends ClientCommand {
    SaveCommand sc;

    @Override
    public boolean execute(String[] args) {
        System.out.println("Closing client...");
        System.exit(0);
        return true;
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "close client application";
    }
}