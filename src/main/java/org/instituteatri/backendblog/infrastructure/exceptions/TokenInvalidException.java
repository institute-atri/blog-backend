package org.instituteatri.backendblog.infrastructure.exceptions;

public class TokenInvalidException extends RuntimeException {
    public TokenInvalidException(String token) {
        super("Token is invalid:" + token);
    }
}