package org.instituteatri.backendblog.infrastructure.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String id) {
        super("Could not find category with id:" + id);
    }
}
