package org.example.product;

public class Person {
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Integer height; // Поле может быть null, Значение поля должно быть больше 0
    private Color hairColor; // Поле может быть null
    private Country nationality; // Поле не может быть null

    // Конструктор
    public Person(String name, Integer height, Color hairColor, Country nationality) {
        this.name = name;
        this.height = height;
        this.hairColor = hairColor;
        this.nationality = nationality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        if (height != null && height <= 0) {
            throw new IllegalArgumentException("The height should be more than 0.");
        }
        this.height = height;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(Country nationality) {
        if (nationality == null) {
            throw new IllegalArgumentException("Национальность не может быть null.");
        }
        this.nationality = nationality;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", height=" + height +
                ", hairColor=" + hairColor +
                ", nationality=" + nationality +
                '}';
    }
}