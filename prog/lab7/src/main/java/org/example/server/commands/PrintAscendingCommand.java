package org.example.server.commands;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.product.Product;

import java.util.stream.Collectors;

public class PrintAscendingCommand extends ServerCommand {
    public PrintAscendingCommand(CollectionManager cm) {
        super("print_ascending", "displays the collection items in ascending order", cm);
    }

    @Override
    public Response execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (!DataBaseManager.checkUserCredentials(login, password)) {
            return new Response("Invalid login or password");
        }

        if (cm.getCollection().isEmpty()) {
            return new Response("Collection is empty");
        }

        String sortedProducts = cm.getCollection().stream()
                .sorted()
                .map(Product::toString)
                .collect(Collectors.joining("\n"));

        return new Response("Elements in ascending order:\n" + sortedProducts);
    }
}
