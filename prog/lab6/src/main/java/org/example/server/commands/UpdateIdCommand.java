package org.example.server.commands;

import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.exceptions.ProductNotFoundException;
import org.example.shared.exceptions.WrongArgumentException;
import org.example.shared.product.Product;

import java.io.IOException;

public class UpdateIdCommand extends ServerCommand{

    public UpdateIdCommand(CollectionManager cm) {
        super("update_id", "updates item with this id", cm);
    }

    @Override
    public Response execute(Request request) throws IOException, WrongArgumentException {
        if (request.getMessage().split(" ")[1] == null || request.getMessage().split(" ")[1].length() < 1) {
            throw new WrongArgumentException("ID is required");
        }

        Long id;
        try {
            id = Long.parseLong(request.getMessage().split(" ")[1]);
            if (id <= 0) {
                throw new WrongArgumentException("ID must be greater than 0");
            }
        } catch (NumberFormatException e) {
            throw new WrongArgumentException("Invalid ID format");
        }

        // Проверка наличия продукта в запросе
        if (request.getProduct() == null) {
            throw new WrongArgumentException("Product data is required for update");
        }

        // Обновление продукта
        boolean productFound = false;
        for (Product product : cm.getCollection()) {
            if (product.getId().equals(id)) {
                productFound = true;
                // Сохраняем старый ID в новом продукте
                request.getProduct().setId(id);
                // Удаляем старый продукт и добавляем новый
                cm.removeProduct(product);
                cm.addProduct(request.getProduct());
                return new Response("Product with ID " + id + " successfully updated");
            }
        }

        if (!productFound) {
            throw new ProductNotFoundException(id);
        }

        return new Response("Update completed"); // Никогда не достигается из-за исключения выше
    }
}
