package org.instituteatri.backendblog.infrastructure.exceptions;

public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException() {
        super("User isn't authenticated.");
    }
}
