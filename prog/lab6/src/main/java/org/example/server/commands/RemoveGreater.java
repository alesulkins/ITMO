package org.example.server.commands;

import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.product.Product;
import org.example.shared.product.comparators.DistanceComparator;

import java.io.IOException;
import java.util.List;

public class RemoveGreater extends ServerCommand{

    public RemoveGreater(CollectionManager cm) {
        super("remove_greater", "removes items which have bigger distance from (0;0) than specified item", cm);
    }

    @Override
    public Response execute(Request request) throws IOException {

        String[] parts = request.getMessage().trim().split(" ");
        if (parts.length < 2) {
            return new Response("Error: ID is required.");
        }


        try {
            long referenceId = Long.parseLong(request.getMessage().split(" ")[1]);
            // Поиск продукта по ID в коллекции
            Product referenceProduct = cm.getCollection().stream()
                    .filter(p -> p.getId().equals(referenceId))
                    .findFirst()
                    .orElse(null);

            if (referenceProduct == null) {
                return new Response("Product with ID " + referenceId + " not found.");
            }

            DistanceComparator comparator = new DistanceComparator(referenceProduct);
            // Фильтрация и удаление
            List<Product> toRemove = cm.getCollection().stream()
                    .filter(comparator::isGreater)
                    .toList();

            cm.getCollection().removeAll(toRemove);
            return new Response("Removed " + toRemove.size() + " elements.");

        } catch (NumberFormatException e) {
            return new Response("Error: Invalid ID format.");
        }
    }
}
