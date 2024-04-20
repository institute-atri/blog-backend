package org.instituteatri.backendblog.infrastructure.exceptions;

public class PasswordsNotMatchException extends RuntimeException {
    public PasswordsNotMatchException() {
        super("The passwords do not match.");
    }
}
