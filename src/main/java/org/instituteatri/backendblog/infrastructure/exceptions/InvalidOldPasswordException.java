package org.instituteatri.backendblog.infrastructure.exceptions;

public class InvalidOldPasswordException extends RuntimeException {
    public InvalidOldPasswordException() {
        super("The old password provided is incorrect.");
    }
}
