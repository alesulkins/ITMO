package org.example.system;

import org.example.manager.CollectionManager;
import org.example.product.Product;
import org.example.util.Console;

import java.util.TreeSet;

/**
 * starts program execution
 */
public class Main {
    /**
     *
     * @param args - command argument
     */
    public static void main(String[] args) {
        try {
            String path = System.getenv("FILE_LAB5");
            System.out.println(path);
            TreeSet<Product> products = ReaderXML.read(path);
            CollectionManager cm = new CollectionManager();
            cm.setCollection(products);
            Console console = new Console(cm);
            console.start();
            System.out.println("1111111111");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}