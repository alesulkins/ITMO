package org.example.server.commands;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.product.Product;

import java.util.Optional;

public class AddIfMinCommand extends ServerCommand {
    public AddIfMinCommand(CollectionManager cm) {
        super("add_if_min", "add product if it is less than the minimum", cm);
    }

    @Override
    public Response execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (!DataBaseManager.checkUserCredentials(login, password)) {
            return new Response("Invalid login or password.");
        }

        Product newProduct = request.getProduct();
        if (newProduct == null) {
            return new Response("Product not provided.");
        }

        Optional<Product> minProduct = cm.getCollection().stream()
                .min(Product::compareTo);

        if (minProduct.isEmpty() || newProduct.compareTo(minProduct.get()) < 0) {
            newProduct.setOwnerLogin(login);

            long inserted = DataBaseManager.insertProduct(newProduct, login);
            if (inserted == -1) {
                return new Response("Failed to insert product into database.");
            }
            newProduct.setId(inserted);
            cm.addProduct(newProduct);
            return new Response("Product added. It is less than the current minimum.");
        } else {
            return new Response("Product not added. It is not less than the current minimum.");
        }
    }
}
