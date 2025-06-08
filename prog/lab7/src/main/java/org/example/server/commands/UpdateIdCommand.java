package org.example.server.commands;

import org.example.server.db.DataBaseManager;
import org.example.server.managers.CollectionManager;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.exceptions.ProductNotFoundException;
import org.example.shared.exceptions.WrongArgumentException;
import org.example.shared.product.Product;

import java.io.IOException;

public class UpdateIdCommand extends ServerCommand {

    public UpdateIdCommand(CollectionManager cm) {
        super("update_id", "updates item with this id", cm);
    }

    @Override
    public Response execute(Request request) throws IOException, WrongArgumentException {
        String login = request.getLogin();
        String password = request.getPassword();

        // 🔐 Проверка авторизации
        if (!DataBaseManager.checkUserCredentials(login, password)) {
            return new Response("⛔ Неверный логин или пароль.");
        }

        String[] parts = request.getMessage().split(" ");
        if (parts.length < 2 || parts[1] == null || parts[1].trim().isEmpty()) {
            throw new WrongArgumentException("ID is required");
        }

        long id;
        try {
            id = Long.parseLong(parts[1]);
            if (id <= 0) throw new WrongArgumentException("ID must be greater than 0");
        } catch (NumberFormatException e) {
            throw new WrongArgumentException("Invalid ID format");
        }

        // 🔍 Проверка владельца
        int productOwnerId = DataBaseManager.getProductOwnerId(id);
        int currentUserId = DataBaseManager.getUserId(login);

        if (productOwnerId == -1) {
            return new Response("product with id " + id + " not found.");
        }

        if (productOwnerId != currentUserId) {
            return new Response("you are not owner of product " + productOwnerId + ". you cannot update this product.");
        }

        Product updatedProduct = request.getProduct();
        if (updatedProduct == null) {
            throw new WrongArgumentException("Product data is required for update");
        }

        updatedProduct.setId(id);
        updatedProduct.setOwnerLogin(login); // 🔐 обязательно сохраняем владельца

        // 💾 Обновляем в БД
        boolean dbSuccess = DataBaseManager.updateProductById(updatedProduct);
        if (!dbSuccess) {
            return new Response("could not update product with id " + id + "in database");
        }

        // 🧠 Обновляем в памяти
        boolean replaced = cm.replaceProductById(id, updatedProduct);
        if (replaced) {
            return new Response("product with id " + id + " successfully updated");
        } else {
            throw new ProductNotFoundException(id);
        }
    }
}
