package org.example.command;

import org.example.manager.CollectionManager;
import org.example.util.Receiver;

/**
 * Базовый интерфейс для реализации команд.
 * Все команды должны реализовывать этот интерфейс.
 */
public interface BaseCommand {

    /**
     * Возвращает название команды.
     *
     * @return Название команды
     */
    String getName();

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды
     */
    String getDescription();

    /**
     * Выполняет команду с переданными параметрами.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param receiver Принимает команды и выполняет их действия
     * @param arg Аргумент команды (если есть)
     * @return Результат выполнения команды
     */
    String execute(CollectionManager manager, Receiver receiver, String arg);
}