package org.example.system.exception;

/**
 * Исключение при отсутствии продукта с указанным ID в коллекции.
 */
public class ProductNotFoundException extends RuntimeException {
    /**
     * Создает исключение с информацией о проблемном ID.
     *
     * @param id ID продукта, который не был найден
     */
    public ProductNotFoundException(Long id) {
        super("Product with ID " + id + " is not found in the collection.");
    }
}
