package org.example.shared.exceptions;

/**
 * Исключение для ошибок валидации данных.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super("Validation error: " + message);
    }

    public ValidationException(String fieldName, String details) {
        super(String.format("Invalid value for field '%s': %s", fieldName, details));
    }
}