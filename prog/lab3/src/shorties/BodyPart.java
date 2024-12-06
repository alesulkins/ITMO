package shorties;

public enum BodyPart    {
    HEAD("голове"),
    ARM("руке"),
    LEG("ноге"),
    CHEST("грудной клетке"),
    BACK("спине");

    private final String name;

    BodyPart(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
