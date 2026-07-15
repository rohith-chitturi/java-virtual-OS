package com.rohith.javavirtualos.exceptions;

/**
 * Thrown when a path is invalid or malformed.
 */
public class InvalidPathException extends FileSystemException {
    public InvalidPathException(String message) {
        super("Invalid path: " + message);
    }
}
