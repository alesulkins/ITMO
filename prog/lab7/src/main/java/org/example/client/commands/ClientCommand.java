package org.example.client.commands;

import java.io.IOException;

public class ClientCommand implements ClientExecutable{
    private String name;
    private String description;

    @Override
    public boolean execute(String[] args) throws IOException {
        return false;
    }

    public String getName() {
        return name;
    }



    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
