package org.instituteatri.backendblog.infrastructure.exceptions;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(String id) {
        super("Could not find helpTag with id:" + id);
    }
}
