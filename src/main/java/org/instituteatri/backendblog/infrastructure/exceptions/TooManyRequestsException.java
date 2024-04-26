package org.instituteatri.backendblog.infrastructure.exceptions;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(String ipAddress) {
        super("Excessive registration requests received from IP address: " + ipAddress);
    }
}
