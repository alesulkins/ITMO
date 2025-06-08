package org.example.server;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;

public class Main {
    static CollectionManager collectionManager = new CollectionManager();

    public static void main(String[] args) {
        try {
            DataBaseManager.connectToDataBase();
            collectionManager.loadCollectionFromDatabase();

            Server server = new Server(collectionManager);
            try {
                server.start(Integer.parseInt(args[0]));
            } finally {
                server.shutdown();
            }

        } catch (Exception e) {
            System.out.println("Startup error: " + e.getMessage());
        }
    }
}
