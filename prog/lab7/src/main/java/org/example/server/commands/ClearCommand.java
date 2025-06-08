package org.example.server.commands;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.product.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ClearCommand extends ServerCommand {
    public ClearCommand(CollectionManager cm) {
        super("clear", "clear all your elements from the collection", cm);
    }

    @Override
    public Response execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (!DataBaseManager.checkUserCredentials(login, password)) {
            return new Response("invalid login or password");
        }

        int userId = DataBaseManager.getUserId(login);
        if (userId == -1) {
            return new Response("user not found.");
        }

        boolean dbResult = DataBaseManager.clearProductsByUserId(userId);
        if (!dbResult) {
            return new Response("could not clear your elements");
        }

        List<Product> toRemove = cm.getCollection().stream()
                .filter(p -> login.equals(p.getOwnerLogin()))
                .collect(Collectors.toList());

        toRemove.forEach(cm::removeProduct);

        return new Response("collection cleared");
    }
}
