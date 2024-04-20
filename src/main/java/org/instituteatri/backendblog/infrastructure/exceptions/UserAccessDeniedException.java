package org.instituteatri.backendblog.infrastructure.exceptions;

public class UserAccessDeniedException extends RuntimeException {
    public UserAccessDeniedException() {
        super("User isn't authorized.");
    }
}
