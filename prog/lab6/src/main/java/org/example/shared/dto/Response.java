package org.example.shared.dto;

import java.io.Serializable;
import java.util.Objects;

public class Response implements Serializable {
    private String response = "";

    public Response(String response) {
        this.response = response;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Response response1)) return false;
        return Objects.equals(response, response1.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(response);
    }

    @Override
    public String toString() {
        return response;
    }

    public String getResponse() {
        return response;
    }
}