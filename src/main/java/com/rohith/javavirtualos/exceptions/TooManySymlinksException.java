package com.rohith.javavirtualos.exceptions;

public class TooManySymlinksException extends FileSystemException {
    public TooManySymlinksException(String path) {
        super("Too many levels of symbolic links: " + path);
    }
}
