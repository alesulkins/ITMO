package org.example.commands.commands;


import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.product.Coordinates;
import org.example.product.Product;
import org.example.product.comparator.DistanceComparator;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Класс команды "remove_greater", удаляет из коллекции все элементы, превышающие заданный.
 * Реализует интерфейс BaseCommand.
 */
public class RemoveGreaterCommand implements BaseCommand {

    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter product ID to compare:");

        try {
            long referenceId = Long.parseLong(scanner.nextLine());

            Product referenceProduct = null;
            for (Product p : manager.getCollection()) {
                if (p.getId().equals(referenceId)) {
                    referenceProduct = p;
                    break;
                }
            }

            if (referenceProduct == null) {
                throw new WrongArgumentException("Product with ID " + referenceId + " not found");
            }

            DistanceComparator comparator = new DistanceComparator(referenceProduct);

            List<Product> toRemove = manager.getCollection().stream()
                    .filter(comparator::isGreater)
                    .toList();

            manager.getCollection().removeAll(toRemove);

            // 5. Выводим результат
            if (toRemove.isEmpty()) {
                System.out.println("No products found with greater distance than product #" + referenceId);
            } else {
                System.out.println("Removed " + toRemove.size() + " elements with greater distance:");
                toRemove.forEach(p -> System.out.println("  " + p));
            }

        } catch (NumberFormatException e) {
            throw new WrongArgumentException("ID must be a number");
        }
    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "remove_greater"
     */
    @Override
    public String getName() {
        return "remove_greater";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "удалить из коллекции все элементы, превышающие заданный"
     */
    @Override
    public String getDescription() {
        return "Remove elements farther from (0,0) than the reference product. " +
                "Outputs list of removed elements";
    }
}