package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.kernel.SecurityManager;
import com.rohith.javavirtualos.kernel.User;

/**
 * Validates filesystem operations before they are executed.
 */
public class FileSystemValidator {

    private static final int MAX_FILENAME_LENGTH = 255;
    private static final String INVALID_CHARS = "*/\\?<>|:\"";
    private SecurityManager securityManager;

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

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

    public void validateCreation(DirectoryNode parent, String name, User currentUser) throws FileSystemException {
        if (securityManager != null && !securityManager.canWrite(currentUser, parent)) {
            throw new FileSystemException("Permission denied");
        }
        validateFileName(name);
        if (parent.hasChild(name)) {
            throw new FileSystemException("A file or directory named '" + name + "' already exists");
        }
    }

    public void validateDeletion(DirectoryNode targetDir, DirectoryNode parentDir, String targetPath, String currentActivePath, User currentUser) throws FileSystemException {
        if (securityManager != null && !securityManager.canWrite(currentUser, parentDir)) {
            throw new FileSystemException("Permission denied");
        }
        if (targetPath.equals("/")) {
            throw new FileSystemException("Cannot delete the root directory");
        }
        // Very basic ancestor check using path strings
        String normalizedTarget = targetPath.endsWith("/") ? targetPath : targetPath + "/";
        String normalizedCurrent = currentActivePath.endsWith("/") ? currentActivePath : currentActivePath + "/";
        if (normalizedCurrent.startsWith(normalizedTarget)) {
            throw new FileSystemException("Cannot delete the current active directory or its ancestors");
        }
    }

    public void validateRead(Inode target, User currentUser) throws FileSystemException {
        if (securityManager != null && !securityManager.canRead(currentUser, target)) {
            throw new FileSystemException("Permission denied");
        }
    }

    public void validateExecute(Inode target, User currentUser) throws FileSystemException {
        if (securityManager != null && !securityManager.canExecute(currentUser, target)) {
            throw new FileSystemException("Permission denied");
        }
    }

    public void validateWrite(Inode target, User currentUser) throws FileSystemException {
        if (securityManager != null && !securityManager.canWrite(currentUser, target)) {
            throw new FileSystemException("Permission denied");
        }
    }
}
