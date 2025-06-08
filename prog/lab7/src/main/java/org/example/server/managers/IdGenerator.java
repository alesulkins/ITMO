package org.example.server.managers;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Генератор уникальных идентификаторов.
 */
public class IdGenerator {

    private static final Set<Long> idSet = new HashSet<>();
    private static final AtomicLong counter = new AtomicLong(1);

    public static long generateId() {
        long id;
        do {
            id = System.nanoTime() + counter.getAndIncrement();
        } while (!idSet.add(id));
        return id;
    }

    public static void remove(long id) {
        idSet.remove(id);
    }
}