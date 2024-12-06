package enterprises;

import java.util.ArrayList;

public class Street {
    private String name;
    private ArrayList<Institution> institutions = new ArrayList<>();

    public Street(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Institution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(ArrayList<Institution> institutions) {
        this.institutions = institutions;
    }
}

