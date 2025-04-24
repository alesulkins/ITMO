package org.example.util;

import org.example.manager.CollectionManager;
import org.example.commands.CommandManager;

import java.util.Scanner;

/**
 * Реализует консольный интерфейс взаимодействия с пользователем.
 * Обеспечивает цикл чтение-выполнение-вывод (REPL).
 */
public class Console {
    private final CommandManager commandManager;
    private final CollectionManager collectionManager;

    public Console(CollectionManager collectionManager) throws Exception {
        this.commandManager = new CommandManager();
        this.collectionManager = collectionManager;
    }

    /**
     * Запускает интерактивную консольную сессию.
     */
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