package org.example.shared.product;

import java.io.Serializable;
import java.util.Objects;

public class Person implements Serializable {
    private final String name;
    private final Integer height;
    private final Color hairColor;
    private final Country nationality;

    public Person(String name, Integer height, Color hairColor, Country nationality) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be null or empty");
        }
        if (height != null && height <= 0) {
            throw new IllegalArgumentException("Height must be greater than 0");
        }
        if (nationality == null) {
            throw new IllegalArgumentException("Nationality cannot be null");
        }

        this.name = name.trim();
        this.height = height;
        this.hairColor = hairColor;
        this.nationality = nationality;
    }

    public String getName() {
        return name;
    }

    public Integer getHeight() {
        return height;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name) &&
                Objects.equals(height, person.height) &&
                hairColor == person.hairColor &&
                nationality == person.nationality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, height, hairColor, nationality);
    }

    @Override
    public String toString() {
        return String.format(
                "Person[name='%s', height=%s, hairColor=%s, nationality=%s]",
                name, height, hairColor, nationality
        );
    }
}