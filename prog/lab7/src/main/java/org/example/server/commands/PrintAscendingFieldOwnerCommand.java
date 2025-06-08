package org.example.server.commands;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.product.Person;
import org.example.shared.product.Product;
import org.example.shared.product.comparators.ComparatorByOwnersHeight;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PrintAscendingFieldOwnerCommand extends ServerCommand {
    public PrintAscendingFieldOwnerCommand(CollectionManager cm) {
        super("print_ascending_owner", "prints elements of the collection in ascending order of the owners", cm);
    }

    @Override
    public Response execute(Request request) throws IOException {
        String login = request.getLogin();
        String password = request.getPassword();

        if (!DataBaseManager.checkUserCredentials(login, password)) {
            return new Response("Invalid login or password");
        }

        List<Person> sortedOwners = cm.getCollection().stream()
                .map(Product::getOwner)
                .filter(Objects::nonNull)
                .sorted(new ComparatorByOwnersHeight())
                .collect(Collectors.toList());

        if (sortedOwners.isEmpty()) {
            return new Response("No products with owners found in collection");
        }

        StringBuilder sb = new StringBuilder("Owners sorted by height:\n");
        sortedOwners.forEach(owner -> {
            String hairColor = owner.getHairColor() != null ?
                    owner.getHairColor().toString() : "null";

            sb.append(String.format(
                    "Name: %-15s | Height: %-4d | Hair: %-6s | Nationality: %s%n",
                    owner.getName(),
                    owner.getHeight(),
                    hairColor,
                    owner.getNationality()
            ));
        });

        return new Response(sb.toString());
    }
}
