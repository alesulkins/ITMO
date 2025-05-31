package org.example.server.commands;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.product.Product;
import java.io.IOException;

/**
 * Класс команды "show", выводит в стандартный поток вывода все элементы коллекции в строковом представлении.
 * Реализует интерфейс BaseCommand.
 */
public class ShowCommand extends ServerCommand {


    public ShowCommand(CollectionManager cm) {
        super("show", "displays the collection", cm);
    }

    @Override
    public Response execute(Request request) throws IOException {
        String info = "";
        for (Product product : cm.getCollection()) {
            info += "\n" +
                    product.toString();
        }
        return new Response(info);
    }
}