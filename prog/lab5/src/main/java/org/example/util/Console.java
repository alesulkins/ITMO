package org.example.util;

import org.example.manager.CollectionManager;
import org.example.manager.CommandManager;

import java.util.Scanner;

public class Console {
    private final CommandManager commandManager;
    private final CollectionManager collectionManager;
    private final Receiver receiver;

    public Console(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        this.commandManager = new CommandManager();
        this.receiver = new Receiver();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome! Enter the command:");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                String result = commandManager.doCommand(input, collectionManager, receiver);
                System.out.println(result);
            }
        }
    }
}