package org.example.manager;

import org.example.product.Product;

import java.util.TreeSet;

public class CollectionManager {
    private TreeSet<Product> collection;


    public CollectionManager() throws Exception {
        collection = new TreeSet<>();
    }


    public TreeSet<Product> getCollection() {
        return collection;
    }

    public void setCollection(TreeSet<Product> collection) {
        this.collection = collection;
    }
}
//todo