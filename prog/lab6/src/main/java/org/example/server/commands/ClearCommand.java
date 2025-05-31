package org.example.server.commands;

import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;

public class ClearCommand extends ServerCommand {

    public ClearCommand(CollectionManager cm) {
        super("clear", "cleans the collection", cm);
    }

    @Override
    public Response execute(Request request) {

        cm.clearCollection();
        return new Response("Collection cleared");
    }
}
