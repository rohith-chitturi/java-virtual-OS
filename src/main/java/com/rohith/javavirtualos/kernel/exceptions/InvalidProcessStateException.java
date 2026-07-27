package com.rohith.javavirtualos.kernel.exceptions;

public class InvalidProcessStateException extends RuntimeException {
    public InvalidProcessStateException(String message) {
        super(message);
    }
}
