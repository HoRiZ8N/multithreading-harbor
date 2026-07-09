package com.inno.harbor.exception;

public class PortException extends RuntimeException {

    public PortException(String message) {
        super(message);
    }

    public PortException(String message, Throwable cause) {
        super(message, cause);
    }
}
