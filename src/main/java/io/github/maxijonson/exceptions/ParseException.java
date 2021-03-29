package io.github.maxijonson.exceptions;

/**
 * Exception for failed parsing of an object to another
 */
public class ParseException extends Exception {
    private static final long serialVersionUID = 1L;

    public ParseException(String message) {
        super(message);
    }

    public ParseException() {
        this("Could not parse the object");
    }
}
