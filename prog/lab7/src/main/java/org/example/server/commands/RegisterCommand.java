package org.example.server.commands;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;

public class RegisterCommand extends ServerCommand {

    public RegisterCommand(CollectionManager cm) {
        super("register", "register a new user", cm);
    }

    @Override
    public Response execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (login == null || password == null) {
            return new Response("login and password must be provided");
        }

        boolean success = DataBaseManager.insertUser(login, password);
        return success
                ? new Response("registration success")
                : new Response("registration failed");
    }
}
