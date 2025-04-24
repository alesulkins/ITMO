package org.example.system.exception;

/**
 * Исключение при передаче неверных аргументов команде.
 */
public class WrongArgumentException extends Exception {
    /**
     * Создает исключение с указанием имени команды.
     *
     * @param commandName Имя команды с некорректными аргументами
     */
    public WrongArgumentException(String commandName) {
        super("Wrong argument for command: " + commandName);
    }
}
