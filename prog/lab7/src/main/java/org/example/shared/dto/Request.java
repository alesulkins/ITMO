package org.example.shared.dto;

import org.example.shared.product.Product;
import java.io.Serializable;
import java.util.*;

public class Request implements Serializable {
    private String message;
    private Product product;
    private String login;
    private String password;


    public Request(String message) {
        this.message = message;
    }

    public Request(String message, String login, String password) {
        this.message = message;
        this.login = login;
        this.password = password;
    }
    public String getMessage() {
        return message;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(message, request.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "Request{" +
                "message='" + message + '\'' +
                '}';
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}