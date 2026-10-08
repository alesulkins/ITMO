package point;

import java.util.ArrayList;
import java.util.List;

public class Store {

    private static final List<Check> history = new ArrayList<>();

    public static synchronized List<Check> history() {
        return history;
    }

    public static synchronized void add(Check check) {
        history.add(check);
    }

    public static synchronized void clear() {
        history.clear();
    }
}
