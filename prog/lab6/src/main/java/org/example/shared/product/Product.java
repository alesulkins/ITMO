package org.example.shared.product;

import org.example.server.managers.IdGenerator;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public class Product implements Serializable, Comparable<Product> {
    private Long id;
    private String name;
    private Coordinates coordinates;
    private ZonedDateTime creationDate;
    private Double price;
    private UnitOfMeasure unitOfMeasure;
    private Person owner;

    public Product(String name, Coordinates coordinates, Double price,
                   UnitOfMeasure unitOfMeasure, Person owner) {
        this.creationDate = ZonedDateTime.now();
        setName(name);
        setCoordinates(coordinates);
        setPrice(price);
        setUnitOfMeasure(unitOfMeasure);
        this.owner = owner;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public ZonedDateTime getCreationDate() { return creationDate; }
    public Double getPrice() { return price; }
    public UnitOfMeasure getUnitOfMeasure() { return unitOfMeasure; }
    public Person getOwner() { return owner; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        this.name = name.trim();
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null)
            throw new IllegalArgumentException("Coordinates cannot be null");
        this.coordinates = coordinates;
    }

    public void setPrice(Double price) {
        if (price == null || price <= 0)
            throw new IllegalArgumentException("Price must be > 0");
        this.price = price;
    }

    public void setUnitOfMeasure(UnitOfMeasure unit) {
        if (unit == null)
            throw new IllegalArgumentException("Unit cannot be null");
        this.unitOfMeasure = unit;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    // Сравнение по расстоянию от (0,0)
    @Override
    public int compareTo(Product other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot compare with null");
        }
        return Double.compare(
                this.calculateDistanceFromOrigin(),
                other.calculateDistanceFromOrigin()
        );
    }

    private double calculateDistanceFromOrigin() {
        return Math.hypot(coordinates.getX(), coordinates.getY());
    }

    // Переопределение equals и hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates.toString() +
                ", price=" + price +
                ", unitOfMeasure=" + unitOfMeasure.toString() +
                ", owner=" + owner +
                ", creationDate=" + creationDate +
                '}';
    }
}