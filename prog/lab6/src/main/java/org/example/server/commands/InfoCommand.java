package org.example.server.commands;

import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import java.time.LocalDateTime;


public class InfoCommand extends ServerCommand{

    public InfoCommand(CollectionManager cm) {
        super("info", "show information about the collection", cm);
    }

    @Override
    public Response execute(Request request){
        System.out.println(LocalDateTime.now());
        String info ="Type of collection: " + cm.getCollection().getClass().getName() + "\n" +
                "Time: " + LocalDateTime.now() + "\n" +
                "Amount of products: " + cm.getCollection().size();
        return new Response(info);
    }
}