package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;

/**
 * Validates filesystem operations before they are executed.
 */
public class FileSystemValidator {

    private static final int MAX_FILENAME_LENGTH = 255;
    private static final String INVALID_CHARS = "*/\\?<>|:\"";

    public void validateFileName(String name) throws FileSystemException {
        if (name == null || name.trim().isEmpty()) {
            throw new FileSystemException("File name cannot be empty");
        }
        if (name.length() > MAX_FILENAME_LENGTH) {
            throw new FileSystemException("File name exceeds maximum length of " + MAX_FILENAME_LENGTH);
        }
        if (name.equals(".") || name.equals("..")) {
            throw new FileSystemException("Invalid file name: '.' and '..' are reserved");
        }
        for (char c : INVALID_CHARS.toCharArray()) {
            if (name.indexOf(c) != -1) {
                throw new FileSystemException("File name contains invalid character: " + c);
            }
        }
    }

    public void validateCreation(DirectoryNode parent, String name) throws FileSystemException {
        validateFileName(name);
        if (parent.hasChild(name)) {
            throw new FileSystemException("A file or directory named '" + name + "' already exists");
        }
    }

    public void validateDeletion(DirectoryNode target, DirectoryNode currentActiveDir) throws FileSystemException {
        if (target.getParent() == null) {
            throw new FileSystemException("Cannot delete the root directory");
        }
        if (isAncestorOrSelf(target, currentActiveDir)) {
            throw new FileSystemException("Cannot delete the current active directory or its ancestors");
        }
    }

    private boolean isAncestorOrSelf(DirectoryNode target, DirectoryNode current) {
        DirectoryNode check = current;
        while (check != null) {
            if (check == target) {
                return true;
            }
            check = check.getParent();
        }
        return false;
    }
}
