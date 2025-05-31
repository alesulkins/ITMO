package org.example.server.managers;

import org.example.shared.product.Product;
import java.util.Stack;

public class CollectionManager {
    private Stack<Product> collection = new Stack<>();

    public void addProduct(Product product) {
        if (product.getId() == null) {
            product.setId(IdGenerator.generateId());
        }
        collection.add(product);

    }

    public void clearCollection() {
        collection.clear();
    }

    public void removeProduct(Product product) {
        collection.remove(product);
    }

    public Stack<Product> getCollection() {
        return collection;
    }

    public void setCollection(Stack<Product> collection) {
        this.collection = collection;
    }
}