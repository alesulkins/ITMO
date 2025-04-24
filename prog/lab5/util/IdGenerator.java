package org.example.util;

/**
 * Генератор уникальных последовательных идентификаторов.
 * Реализует потокобезопасное получение ID.
 */
public class IdGenerator {
    private static long idCounter = 1;

    /**
     * Генерирует новый уникальный ID.
     *
     * @return Сгенерированный числовой ID
     */
    public static synchronized long generateId() {
        return idCounter++;
    }

    public static long getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(long idCounter) {
        IdGenerator.idCounter = idCounter;
    }
}