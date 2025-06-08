package org.example.server.commands;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.exceptions.WrongArgumentException;
import org.example.shared.product.Product;
import org.example.shared.product.comparators.DistanceComparator;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveGreater extends ServerCommand{

    public RemoveGreater(CollectionManager cm) {
        super("remove_greater", "removes items which have bigger distance from (0;0) than specified item", cm);
    }

    @Override
    public Response execute(Request request) throws IOException, WrongArgumentException {
        String login = request.getLogin();
        String password = request.getPassword();

        if (!DataBaseManager.checkUserCredentials(login, password)) {
            return new Response("Invalid login or password.");
        }

        Product reference = request.getProduct();
        if (reference == null) {
            return new Response("Reference product not provided.");
        }

        List<Product> toRemove = cm.getCollection().stream()
                .filter(p -> login.equals(p.getOwnerLogin()))
                .filter(p -> p.compareTo(reference) > 0)
                .collect(Collectors.toList());

        int deletedCount = 0;
        for (Product p : toRemove) {
            if (DataBaseManager.removeProductById(p.getId(), login)) {
                cm.removeProduct(p);
                deletedCount++;
            }
        }

        if (deletedCount == 0) {
            return new Response("No products were removed.");
        } else {
            return new Response("Removed " + deletedCount + " products greater than the given one.");
        }
    }
}
