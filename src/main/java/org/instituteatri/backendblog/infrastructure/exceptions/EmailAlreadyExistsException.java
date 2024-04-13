package org.instituteatri.backendblog.infrastructure.exceptions;

public class EmailAlreadyExistsException  extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("Email already exists.");
    }
}
