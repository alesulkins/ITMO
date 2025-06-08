package org.example.server.commands;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;

import java.time.LocalDateTime;

public class InfoCommand extends ServerCommand {

    public InfoCommand(CollectionManager cm) {
        super("info", "show information about the collection", cm);
    }

    @Override
    public Response execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (!DataBaseManager.checkUserCredentials(login, password)) {
            return new Response("Invalid login or password");
        }

        String info = "Collection type: " + cm.getCollection().getClass().getSimpleName() + "\n" +
                "Current time: " + LocalDateTime.now() + "\n" +
                "Number of products: " + cm.getCollection().size();

        return new Response(info);
    }
}
