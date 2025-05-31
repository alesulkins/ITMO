package org.example.server.commands;
import org.example.shared.dto.Request;
import org.example.shared.dto.Response;
import org.example.shared.exceptions.WrongArgumentException;

import java.io.IOException;

public interface ServerExecutable {
    public Response execute(Request request) throws IOException, WrongArgumentException;

}