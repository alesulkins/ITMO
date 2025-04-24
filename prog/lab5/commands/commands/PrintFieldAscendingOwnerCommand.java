package org.example.commands.commands;


import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.product.Person;
import org.example.product.Product;
import org.example.product.comparator.ComparatorByOwnersHeight;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Класс команды "print_field_ascending_owner", выводит значения поля owner всех элементов коллекции в порядке возрастания.
 * Реализует интерфейс BaseCommand.
 */
public class PrintFieldAscendingOwnerCommand implements BaseCommand {

    /**
     * Выполняет команду вывода значений поля owner всех элементов в порядке возрастания.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param commandManager Принимает команды и выполняет их действия
     * @param args Аргумент команды, не используется в данной команде
     */
    @Override
    public void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException {
        List<Person> sortedOwners = manager.getCollection().stream()
                .map(Product::getOwner)
                .filter(Objects::nonNull)
                .sorted(new ComparatorByOwnersHeight())
                .toList();

        if (sortedOwners.isEmpty()) {
            System.out.println("No products with owners found in collection");
        }

        // Выводим результат
        System.out.println("Owners sorted by height in ascending order:");
        sortedOwners.forEach(owner -> {
            String hairColor = owner.getHairColor() != null ? owner.getHairColor().toString() : "null";
            System.out.printf("Name: %-15s | Height: %-4d | Hair: %-6s | Nationality: %s%n",
                    owner.getName(),
                    owner.getHeight(),
                    hairColor,
                    owner.getNationality());
        });
    }

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "print_field_ascending_owner"
     */
    @Override
    public String getName() {
        return "print_field_ascending_owner";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "вывести значения поля owner всех элементов в порядке возрастания"
     */
    @Override
    public String getDescription() {
        return "print the values of the owner's height field of all elements in ascending order";
    }


}