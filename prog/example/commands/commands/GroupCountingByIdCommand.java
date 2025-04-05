package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.system.exception.WrongArgumentException;
import org.example.product.Product;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс команды "group_counting_by_id", группирует элементы коллекции
 * по значению поля id и выводит количество элементов в каждой группе.
 * Реализует интерфейс BaseCommand.
 */
public class GroupCountingByIdCommand implements BaseCommand {

    /**
     * Выполняет команду группировки элементов коллекции по значению поля id.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param commandManager Принимает команды и выполняет их действия
     * @param args Аргумент команды, не используется в данной команде
     */
    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        // Группируем продукты по ID
        Map<Long, Long> idCounts = manager.getCollection().stream()
                .collect(Collectors.groupingBy(
                        Product::getId,
                        Collectors.counting()
                ));

        if (idCounts.isEmpty()) {
            System.out.println("Collection is empty");
            return;
        }

        // Выводим результаты
        System.out.println("Element counts by ID:");
        idCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.printf("ID %d: %d elements%n", entry.getKey(), entry.getValue())
                );

        System.out.printf("Total unique IDs: %d%n", idCounts.size());
    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "group_counting_by_id"
     */
    @Override
    public String getName() {
        return "group_counting_by_id";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "сгруппировать элементы коллекции по значению поля id, вывести количество элементов в каждой группе"
     */
    @Override
    public String getDescription() {
        return "groups the collection items by the value of the id field, outputs the number of items in each group";
    }


}