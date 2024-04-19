package org.instituteatri.backendblog.infrastructure.exceptions;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String id) {
        super("Could not find post with id:" + id);
    }
}
