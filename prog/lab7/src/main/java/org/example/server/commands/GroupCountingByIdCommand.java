package org.example.server.commands;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.product.Product;

import java.util.Map;
import java.util.stream.Collectors;

public class GroupCountingByIdCommand extends ServerCommand {

    public GroupCountingByIdCommand(CollectionManager cm) {
        super("group_counting_by_id", "count products by id", cm);
    }

    @Override
    public Response execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (!DataBaseManager.checkUserCredentials(login, password)) {
            return new Response("Invalid login or password");
        }

        Map<Long, Long> idGroups = cm.getCollection().stream()
                .collect(Collectors.groupingBy(Product::getId, Collectors.counting()));

        if (idGroups.isEmpty()) {
            return new Response("Collection is empty");
        }

        String result = idGroups.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> String.format("ID %d: %d elements", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));

        return new Response(
                "Element counts by ID:\n" + result + "\nTotal unique IDs: " + idGroups.size()
        );
    }
}
