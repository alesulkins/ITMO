package org.example.command;

import org.example.manager.CollectionManager;
import org.example.product.Product;
import org.example.util.Receiver;

import java.util.Scanner;

/**
 * Класс команды "remove_greater", удаляет из коллекции все элементы, превышающие заданный.
 * Реализует интерфейс BaseCommand.
 */
public class RemoveGreaterCommand implements BaseCommand {

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
        return "delete all items from the collection that exceed the specified size.";
    }

    /**
     * Выполняет команду удаления элементов из коллекции, превышающих заданный элемент.
     * Запрашивает данные для создания элемента для сравнения.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        Scanner scanner = new Scanner(System.in);

// Создание продукта для сравнения
        Product comparisonProduct = receiver.createProduct();

        return receiver.removeGreater(manager, comparisonProduct);
    }
}