package com.junior.cadastro.exceptions;

public class PluggyIntegrationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PluggyIntegrationException(String message) {
        super(message);
    }

    public PluggyIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}