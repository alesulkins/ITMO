package org.example.shared.exceptions;


import java.io.Serializable;

/**
 * Исключение при передаче неверных аргументов команде.
 */
public class WrongArgumentException extends Exception implements Serializable {
    /**
     * Создает исключение с указанием имени команды.
     *
     * @param commandName Имя команды с некорректными аргументами
     */
    public WrongArgumentException(String commandName) {
        super("Wrong argument for command: " + commandName);
    }
}
