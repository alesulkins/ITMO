package org.example.server.commands;

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
        Product newProduct = request.getProduct();
        Optional<Product> minProduct = cm.getCollection().stream()
                .min(Product::compareTo);

        if (minProduct.isEmpty() || newProduct.compareTo(minProduct.get()) < 0) {
            cm.addProduct(newProduct);
            return new Response("Product added (new minimum)");
        } else {
            return new Response("Product not added - not smaller than current minimum");
        }
    }
}