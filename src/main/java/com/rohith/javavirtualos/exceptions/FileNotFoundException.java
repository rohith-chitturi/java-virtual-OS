package com.rohith.javavirtualos.exceptions;

/**
 * Thrown when a file or directory cannot be found.
 */
public class FileNotFoundException extends FileSystemException {
    public FileNotFoundException(String path) {
        super("No such file or directory: " + path);
    }
}
