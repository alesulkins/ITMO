package org.example.shared.exceptions;

import java.io.Serializable;

public class ProductNotFoundException extends RuntimeException implements Serializable {
    /**
     * Создает исключение с информацией о проблемном ID.
     *
     * @param id ID продукта, который не был найден
     */
    public ProductNotFoundException(Long id) {
        super("Product with ID " + id + " is not found in the collection.");
    }
}