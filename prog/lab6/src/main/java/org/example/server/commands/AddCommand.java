package org.example.server.commands;

import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import java.io.IOException;

public class AddCommand extends ServerCommand {
    public AddCommand(CollectionManager cm) {
        super("add", "add product into the collection", cm);
    }

    @Override
    public Response execute(Request request) throws IOException {
        cm.addProduct(request.getProduct());
        return new Response("Successfully added!");
    }
}
