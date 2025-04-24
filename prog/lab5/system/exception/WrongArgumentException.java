package org.example.system.exception;

public class WrongArgumentException extends Exception{
    public WrongArgumentException(String commandName) {
        super("Wrong argument for command: " + commandName);
    }
}
