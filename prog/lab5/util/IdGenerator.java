package org.example.util;

public class IdGenerator {
    private static long idCounter = 1;

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