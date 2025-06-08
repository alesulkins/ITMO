package org.example.server.commands;

import org.example.server.db.DataBaseManager;
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
        String login = request.getLogin();
        String password = request.getPassword();

        if (!DataBaseManager.checkUserCredentials(login, password)) {
            return new Response("invalid login or password");
        }

        String[] args = request.getMessage().split(" ");
        if (args.length < 2) {
            return new Response("enter the id remove_by_id <id>");
        }

        try {
            long productId = Long.parseLong(args[1]);

            int productOwnerId = DataBaseManager.getProductOwnerId(productId);
            int currentUserId = DataBaseManager.getUserId(login);

            if (productOwnerId == -1) {
                return new Response("product with such id not found");
            }

            if (productOwnerId != currentUserId) {
                return new Response("you don't have enough rights to remove this item");
            }

            boolean removedFromDb = DataBaseManager.removeProductById(productId, login);

            if (removedFromDb) {
                Product target = cm.getCollection().stream()
                        .filter(p -> p.getId().equals(productId))
                        .findFirst()
                        .orElse(null);

                if (target != null) {
                    cm.removeProduct(target);
                }

                return new Response("product removed from the collection");
            } else {
                return new Response("couldn't remove product from the collection");
            }

        } catch (NumberFormatException e) {
            return new Response("ID should be an integer");
        }
    }
}
