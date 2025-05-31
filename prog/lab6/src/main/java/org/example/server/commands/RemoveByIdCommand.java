package org.example.server.commands;

import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.product.Product;

public class RemoveByIdCommand extends ServerCommand {

    public RemoveByIdCommand(CollectionManager cm) {
        super("remove_by_id", "remove item with this id from the collection", cm);
    }

    @Override
    public Response execute(Request request) {
        System.out.println(request.getMessage());
        if (request.getMessage().split(" ").length != 2) {
            return new Response("Error: ID is required.");
        }

        try {
            long id = Long.parseLong(request.getMessage().split(" ")[1]);

            Product target = cm.getCollection().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (target == null) {
                return new Response("Product with ID " + id + " not found.");
            }
            cm.removeProduct(target);
            return new Response("Product with ID " + id + " removed.");
        } catch (NumberFormatException e) {
            return new Response("Error: Invalid ID format. Must be a number.");
        }
    }
}