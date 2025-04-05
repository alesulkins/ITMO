package org.example.commands.commands;

import org.example.commands.CommandManager;
import org.example.manager.CollectionManager;
import org.example.system.exception.WrongArgumentException;

import java.io.IOException;

/**
 * Базовый интерфейс для реализации команд.
 * Все команды должны реализовывать этот интерфейс.
 */
public interface BaseCommand {

    /**
     * Выполняет команду с переданными параметрами.
     *
     * @param manager Менеджер коллекции, управляющий коллекцией продуктов
     * @param commandManager Принимает команды и выполняет их действия
     * @param args Аргумент команды (если есть)
     */
    void execute(String[] args, CollectionManager manager, CommandManager commandManager) throws WrongArgumentException, IOException;


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
}