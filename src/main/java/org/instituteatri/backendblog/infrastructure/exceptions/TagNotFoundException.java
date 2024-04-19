package org.instituteatri.backendblog.infrastructure.exceptions;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(String id) {
        super("Could not find tag with id:" + id);
    }
}
