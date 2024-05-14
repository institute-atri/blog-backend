package org.instituteatri.backendblog.infrastructure.exceptions;

public class CustomExceptionEntities extends RuntimeException {
    public CustomExceptionEntities(String message) {
        super(message);
    }
}
