package org.example.util;

import org.example.manager.CollectionManager;
import org.example.commands.CommandManager;
import java.util.Scanner;

public class Console {
    private final CommandManager commandManager;
    private final CollectionManager collectionManager;

    public Console(CollectionManager collectionManager) throws Exception {
        this.commandManager = new CommandManager();
        this.collectionManager = collectionManager;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome! Enter the commands:");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                commandManager.execute(input);
            }
        }
    }
}