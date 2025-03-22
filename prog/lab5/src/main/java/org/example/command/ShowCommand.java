package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Класс команды "show", выводит в стандартный поток вывода все элементы коллекции в строковом представлении.
 * Реализует интерфейс BaseCommand.
 */
public class ShowCommand implements BaseCommand {

    /**
     * Возвращает имя команды.
     *
     * @return Название команды "show"
     */
    @Override
    public String getName() {
        return "show";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды "вывести в стандартный поток вывода все элементы коллекции в строковом представлении"
     */
    @Override
    public String getDescription() {
        return "output all the elements of the collection in a string representation to the standard output stream";
    }

    /**
     * Выполняет команду вывода всех элементов коллекции в строковом представлении.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды, не используется в данной команде
     * @return Результат выполнения команды
     */
    @Override
    public String execute(CollectionManager manager, Receiver receiver, String arg) {
        return receiver.showProducts(manager);
    }
}