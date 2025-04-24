package org.example.system;

import org.example.manager.CollectionManager;
import org.example.product.Product;
import org.example.util.Console;

import java.io.FileInputStream;
import java.util.Stack;

/**
 * starts program execution
 */
public class Main {
    /**
     *
     * @param args - commands argument
     */
    public static void main(String[] args) {
        try {
            String path = System.getenv("FILE_LAB5");
            Stack<Product> products = ReaderXML.read(new FileInputStream(path));
            CollectionManager cm = new CollectionManager();
            cm.setCollection(products);

            Console console = new Console(cm);
            console.start();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}