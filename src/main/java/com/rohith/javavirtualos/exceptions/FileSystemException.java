package com.rohith.javavirtualos.exceptions;

/**
 * Base exception for all file system related errors.
 */
public class FileSystemException extends RuntimeException {
    public FileSystemException(String message) {
        super(message);
    }
}
