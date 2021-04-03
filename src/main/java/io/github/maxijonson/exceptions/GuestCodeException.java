package io.github.maxijonson.exceptions;

public class GuestCodeException extends Exception {
    private static final long serialVersionUID = 1L;

    public GuestCodeException(String message) {
        super(message);
    }

    public GuestCodeException() {
        this("There was an error setting the guest code");
    }
}
