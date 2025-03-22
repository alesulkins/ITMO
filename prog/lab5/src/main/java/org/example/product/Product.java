package org.example.product;

import org.example.util.IdGenerator;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Product implements Comparable<Product> {
    private Long id; // Поле не может быть null, Значение поля должно быть больше 0, Уникальное, Генерируется автоматически
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private ZonedDateTime creationDate; // Поле не может быть null, Генерируется автоматически
    private Double price; // Поле не может быть null, Значение поля должно быть больше 0
    private UnitOfMeasure unitOfMeasure; // Поле не может быть null
    private Person owner; // Поле может быть null

    public Product(String name, Coordinates coordinates, Double price, UnitOfMeasure unitOfMeasure, Person owner) {
        this.id = IdGenerator.generateId(); // Генерация уникального id через IdGenerator
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.now(); // Генерация текущей даты и времени
        this.price = price;
        this.unitOfMeasure = unitOfMeasure;
        this.owner = owner;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID should be more than 0.");
        }
        this.id = id;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null oe empty");
        }
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null.");
        }
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Price cannot be 0 ");
        }
        this.price = price;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        if (unitOfMeasure == null) {
            throw new IllegalArgumentException("Unit of measure cannot be null.");
        }
        this.unitOfMeasure = unitOfMeasure;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    // sort by id
    @Override
    public int compareTo(Product other) {
        return this.id.compareTo(other.getId());
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates.toString() +
                ", creationDate=" + creationDate +
                ", price=" + price +
                ", unitOfMeasure=" + unitOfMeasure.toString() +
                ", owner=" + owner +
                '}';
    }
    public String toXML() {
        StringBuilder xml = new StringBuilder("<product>");

        xml.append("\n\t\t<id>").append(id).append("</id>");
        xml.append("\n\t\t<name>").append(name).append("</name>");
        xml.append("\n\t\t<coordinates>");
        xml.append("\n\t\t\t<x>").append(coordinates.getX()).append("</x>");
        xml.append("\n\t\t\t<y>").append(coordinates.getY()).append("</y>");
        xml.append("\n\t\t</coordinates>");
        xml.append("\n\t\t<creationDate>").append(creationDate.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)).append("</creationDate>");
        xml.append("\n\t\t<price>").append(price).append("</price>");
        xml.append("\n\t\t<unitOfMeasure>").append(unitOfMeasure).append("</unitOfMeasure>");

        if (owner != null) {
            xml.append("\n\t\t<owner>");
            xml.append("\n\t\t\t<name>").append(owner.getName()).append("</name>");
            if (owner.getHeight() != null) {
                xml.append("\n\t\t\t<height>").append(owner.getHeight()).append("</height>");
            }
            if (owner.getHairColor() != null) {
                xml.append("\n\t\t\t<hairColor>").append(owner.getHairColor()).append("</hairColor>");
            }
            xml.append("\n\t\t\t<nationality>").append(owner.getNationality()).append("</nationality>");
            xml.append("\n\t\t</owner>");
        }

        xml.append("\n\t</product>");
        return xml.toString();
    }
}