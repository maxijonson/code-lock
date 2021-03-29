package io.github.maxijonson.exceptions;

/**
 * Exception related to commands
 */
public class CommandException extends Exception {
    private static final long serialVersionUID = 1L;

    public CommandException(String message) {
        super(message);
    }

    public CommandException() {
        this("Invalid usage. use '/codelock help' for commands info");
    }
}
