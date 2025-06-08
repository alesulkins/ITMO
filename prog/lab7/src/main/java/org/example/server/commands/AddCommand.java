package org.example.server.commands;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.product.Product;

import java.io.IOException;

public class AddCommand extends ServerCommand {
    public AddCommand(CollectionManager cm) {
        super("add", "add product into the collection", cm);
    }

    @Override
    public Response execute(Request request) throws IOException {
        String login = request.getLogin();
        String password = request.getPassword();

        if (!DataBaseManager.checkUserCredentials(login, password)) {
            return new Response("Invalid login or password");
        }

        Product product = request.getProduct();
        product.setOwnerLogin(login);

        long dbSuccess = DataBaseManager.insertProduct(product, login);
        if (dbSuccess == -1) {
            return new Response("Database error: could not add product");
        }
        product.setId(dbSuccess);
        cm.addProduct(product);
        return new Response("Product added successfully");
    }
}
