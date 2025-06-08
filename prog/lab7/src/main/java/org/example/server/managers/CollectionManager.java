package org.example.server.managers;

import org.example.server.db.DataBaseManager;
import org.example.shared.product.Product;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CollectionManager {
    private ConcurrentLinkedDeque<Product> collection = new ConcurrentLinkedDeque<>();

    public void addProduct(Product product) {
        if (product.getId() == null) {
            product.setId(IdGenerator.generateId());
        }
        collection.add(product);

    }

    public void loadCollectionFromDatabase() {
        this.collection = new ConcurrentLinkedDeque<>(DataBaseManager.loadAllProducts());
        System.out.println("Collection loaded from database. Size: " + collection.size());
    }

    public void removeProduct(Product product) {
        collection.remove(product);
    }

    public ConcurrentLinkedDeque<Product> getCollection() {
        return collection;
    }

    public boolean replaceProductById(long id, Product newProduct) {
        for (Product product : collection) {
            if (product.getId().equals(id)) {
                collection.remove(product);
                collection.add(newProduct);
                return true;
            }
        }
        return false;
    }

    public void setCollection(ConcurrentLinkedDeque<Product> collection) {
        this.collection = collection;
    }
}