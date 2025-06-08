package org.example.server.commands;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;

public class LoginCommand extends ServerCommand {

    public LoginCommand(CollectionManager cm) {
        super("login", "login with username and password", cm);
    }

    @Override
    public Response execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (login == null || password == null) {
            return new Response("login and password must be provided");
        }

        boolean valid = DataBaseManager.checkUserCredentials(login, password);
        return valid
                ? new Response("login success")
                : new Response("no such user or wrong password");
    }
}
