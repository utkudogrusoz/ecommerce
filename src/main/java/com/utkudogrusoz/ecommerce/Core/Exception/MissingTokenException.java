package com.utkudogrusoz.ecommerce.Core.Exception;

public class MissingTokenException extends RuntimeException {
    public MissingTokenException(String message) {
        super(message);
    }
}
