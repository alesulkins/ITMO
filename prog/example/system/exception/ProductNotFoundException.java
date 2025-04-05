package org.example.system.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(Long id) {
        super("Product with ID " + id + " is not found in the collection.");
    }
}
