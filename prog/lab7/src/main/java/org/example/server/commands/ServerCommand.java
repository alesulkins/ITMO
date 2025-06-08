package org.example.server.commands;

import org.example.server.managers.CollectionManager;

public abstract class ServerCommand implements ServerExecutable {

    private String name;
    private String description;
    CollectionManager cm = new CollectionManager();

    public ServerCommand(String name, String description, CollectionManager cm){
        this.cm = cm;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ServerCommand command = (ServerCommand) obj;
        return name.equals(command.name) && description.equals(command.description);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + description.hashCode();
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}