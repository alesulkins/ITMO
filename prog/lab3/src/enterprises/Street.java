package enterprises;

import java.util.ArrayList;

public class Street {
    private final String name;
    private final ArrayList<Institution> institutions = new ArrayList<>();

    public Street(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Institution> getInstitutions() {
        return institutions;
    }

}

