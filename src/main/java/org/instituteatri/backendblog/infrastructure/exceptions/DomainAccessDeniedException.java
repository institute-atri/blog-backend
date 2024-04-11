package org.instituteatri.backendblog.infrastructure.exceptions;

public class DomainAccessDeniedException extends RuntimeException {
    public DomainAccessDeniedException() {
        super("User isn't authorized.");
    }
}
