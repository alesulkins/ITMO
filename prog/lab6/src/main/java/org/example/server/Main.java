package org.example.server;

import org.example.server.commands.SaveCommand;
import org.example.server.managers.CollectionManager;
import org.example.server.system.ReaderXML;
import org.example.shared.product.Product;
import java.io.FileInputStream;
import java.util.Stack;

public class Main {
    static CollectionManager collectionManager = new CollectionManager();
    static SaveCommand sc = new SaveCommand(collectionManager);
    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                sc.execute();
                System.out.println(sc.execute());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }));

        try {
            Stack<Product> products = ReaderXML.read(new FileInputStream(args[0]));
            collectionManager.setCollection(products);
            Server server = new Server(collectionManager);
            server.start(Integer.parseInt(args[1]));
        } catch (Exception e) {
        System.out.println(e.getMessage());
        }
    }
}
