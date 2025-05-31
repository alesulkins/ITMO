package org.example.client.commands;

import java.io.IOException;

public interface ClientExecutable {
    boolean execute(String[] args) throws IOException;
}

