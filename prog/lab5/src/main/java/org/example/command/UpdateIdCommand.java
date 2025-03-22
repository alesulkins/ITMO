package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

import java.util.Scanner;

/**
 * Класс команды "update id", обновляет значение элемента коллекции, id которого равен заданному.
 * Реализует интерфейс BaseCommand.
 */
public class UpdateIdCommand implements BaseCommand {

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "update id"
     */
    @Override
    public String getName() {
        return "update id";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "обновить значение элемента коллекции, id которого равен заданному"
     */
    @Override
    public String getDescription() {
        return "updates the value of a collection item whose id is equal to the specified one";
    }

    /**
     * Выполняет команду обновления элемента коллекции с заданным id.
     * Запрашивает id элемента для обновления и выполняет обновление.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the ID of the item to update:");
        Long id = null;
        while (id == null) {
            try {
                id = Long.parseLong(scanner.nextLine());
                if (id <= 0) {
                    System.out.println("ID shou[d be more than 0. Try again:");
                    id = null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input. Enter the number:");
            }
        }

        return receiver.updateId(manager, id);
    }
}