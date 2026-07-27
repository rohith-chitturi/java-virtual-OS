package com.rohith.javavirtualos.kernel.exceptions;

public class ProcessNotFoundException extends RuntimeException {
    public ProcessNotFoundException(String message) {
        super(message);
    }
}
