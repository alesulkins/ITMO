package org.example.util;

import org.example.product.Product;
import org.example.product.UnitOfMeasure;

import java.util.ArrayList;
import java.util.Arrays;

public class Validator {

    private ArrayList<Long> ids = new ArrayList<>();
    private IdGenerator idGen = new IdGenerator();
    private ArrayList<UnitOfMeasure> possibleUnitsOfMeasure = new ArrayList<>(Arrays.asList(
            UnitOfMeasure.CENTIMETERS,
            UnitOfMeasure.LITERS,
            UnitOfMeasure.MILLILITERS,
            UnitOfMeasure.MILLIGRAMS
    ));

    /**
     * Проверяет уникальность ID продукта.
     * Если ID уже существует, генерирует новый уникальный ID.
     * @param product Объект Product, который нужно проверить.
     * @return Объект Product с уникальным ID.
     */
    public Product getValidatedId(Product product) {
        Long id = product.getId();
        if (ids.contains(id)) {
            Long tmpId = idGen.generateId();
            System.out.println("ID продукта " + product.getId() + " заменен на " + tmpId + " из-за коллизии.");
            product.setId(tmpId);
            ids.add(tmpId);
            return product;
        } else {
            ids.add(product.getId());
            return product;
        }
    }

    /**
     * Проверяет корректность всех полей объекта Product.
     * @param product Объект Product, который нужно проверить.
     * @return Объект Product, если все поля корректны, иначе null.
     */
    public Product getValid(Product product) {
        if (
                product.getId() <= 0 ||
                        product.getName().isBlank() ||
                        product.getName().isEmpty() ||
                        product.getName() == null ||
                        product.getCoordinates() == null ||
                        product.getCoordinates().getY() > 696 ||
                        product.getCreationDate() == null ||
                        product.getPrice() <= 0 ||
                        product.getUnitOfMeasure() == null ||
                        !possibleUnitsOfMeasure.contains(product.getUnitOfMeasure())

        ) {
            return null;
        } else {
            if (product.getOwner() != null) {
                if (
                        product.getOwner().getName().isEmpty() ||
                                product.getOwner().getName().isBlank() ||
                                product.getOwner().getName() == null ||
                                product.getOwner().getHeight() == null ||
                                product.getOwner().getHeight() < 0 ||
                                product.getOwner().getHairColor() == null ||
                                product.getOwner().getNationality() == null
                ) {
                    return null;
                } else {
                    return product;
                }
            } else {
                return product;
            }
        }
    }
}