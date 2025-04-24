package org.example.manager;

import org.example.product.Product;

import java.util.Stack;

/**
 * Управляет коллекцией продуктов, хранящейся в стеке.
 * Обеспечивает добавление, удаление и очистку элементов с проверкой уникальности.
 */
public class CollectionManager {
    private Stack<Product> collection = new Stack<>();

    public void setCollection(Stack<Product> collection) {
        if (collection != null) {
            this.collection = collection;
        }
    }

    /**
     * Добавляет продукт в коллекцию, если он уникален.
     *
     * @param product Объект продукта для добавления
     */
    public void addProduct(Product product) {
        for (Product p : collection) {
            if (p.equals(product)) {
                return;
            }
        }
        collection.add(product); // <-- добавляем в коллекцию
    }

    public void removeProduct(Product product){
        for (Product p : collection) {
            if (p.equals(product)) {
                collection.remove(product);
            }
        }
    }

    public void clearCollection(){
        collection.clear();
    }

    public Stack<Product> getCollection() {
        return collection;
    }
}
