package org.instituteatri.backendblog.infrastructure.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super("Could not find helpUser with id:" + id);
    }
}
