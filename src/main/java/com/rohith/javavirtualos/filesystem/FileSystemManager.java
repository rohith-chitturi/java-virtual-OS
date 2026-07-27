package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.events.EventBus;
import com.rohith.javavirtualos.exceptions.FileNotFoundException;
import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.filesystem.model.DeviceNode;
import com.rohith.javavirtualos.kernel.SecurityManager;
import com.rohith.javavirtualos.kernel.User;

/**
 * Central orchestrator for all in-memory file system operations.
 */
public class FileSystemManager {

    private final DirectoryNode root;
    private final PathResolver pathResolver;
    private final FileSystemValidator validator;
    @SuppressWarnings("unused")
    private final EventBus eventBus;
    
    @SuppressWarnings("unused")
    private SecurityManager securityManager;

    public FileSystemManager() {
        this.root = new DirectoryNode("", "root", null);
        this.pathResolver = new PathResolver(this.root);
        this.validator = new FileSystemValidator();
        this.eventBus = null; // Stub until Events module is fully built
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
        this.validator.setSecurityManager(securityManager);
    }

    public DirectoryNode getRoot() {
        return root;
    }

    public Inode resolvePath(String path, DirectoryNode currentDir) {
        return pathResolver.resolvePath(path, currentDir);
    }

    public DirectoryNode resolveDirectory(String path, DirectoryNode currentDir) throws FileSystemException {
        Inode node = pathResolver.resolvePath(path, currentDir);
        if (node == null) {
            throw new FileNotFoundException(path);
        }
        if (!(node instanceof DirectoryNode)) {
            throw new FileSystemException(path + " is not a directory");
        }
        return (DirectoryNode) node;
    }

    public void createDirectory(String path, DirectoryNode currentDir, User currentUser) throws FileSystemException {
        DirectoryNode parent = pathResolver.resolveParentDirectory(path, currentDir);
        if (parent == null) throw new FileNotFoundException("Parent directory does not exist");
        
        String name = pathResolver.extractName(path);
        validator.validateCreation(parent, name, currentUser);

        DirectoryNode newDir = new DirectoryNode(name, currentUser.getUsername(), parent);
        parent.addChild(newDir);
        
        // Emitting event (when EventBus is ready)
        // eventBus.publish(new FileSystemEvent("DIRECTORY_CREATED", newDir.getAbsolutePath()));
    }

    public void createFile(String path, DirectoryNode currentDir, User currentUser) throws FileSystemException {
        DirectoryNode parent = pathResolver.resolveParentDirectory(path, currentDir);
        if (parent == null) throw new FileNotFoundException("Parent directory does not exist");
        
        String name = pathResolver.extractName(path);
        validator.validateCreation(parent, name, currentUser);

        FileNode newFile = new FileNode(name, currentUser.getUsername(), parent);
        parent.addChild(newFile);
    }

    public void remove(String path, DirectoryNode currentDir, boolean isDirectoryCommand, User currentUser) throws FileSystemException {
        Inode target = pathResolver.resolvePath(path, currentDir);
        if (target == null) throw new FileNotFoundException(path);

        if (isDirectoryCommand && !(target instanceof DirectoryNode)) {
            throw new FileSystemException(path + " is not a directory");
        }
        if (!isDirectoryCommand && target instanceof DirectoryNode) {
            throw new FileSystemException(path + " is a directory");
        }
        if (target instanceof DirectoryNode && !((DirectoryNode) target).getChildren().isEmpty()) {
            throw new FileSystemException("Directory not empty");
        }

        DirectoryNode targetDir = target instanceof DirectoryNode ? (DirectoryNode) target : null;
        if (targetDir != null) {
            validator.validateDeletion(targetDir, currentDir, currentUser);
        } else {
            validator.validateWrite(target, currentUser);
        }

        target.getParent().removeChild(target.getName());
    }
    public void validateReadAccess(Inode target, User currentUser) throws FileSystemException {
        validator.validateRead(target, currentUser);
    }
    
    public void validateWriteAccess(Inode target, User currentUser) throws FileSystemException {
        validator.validateWrite(target, currentUser);
    }
    
    public void mountDevice(String path, DeviceNode deviceNode) throws FileSystemException {
        DirectoryNode parent = pathResolver.resolveParentDirectory(path, root);
        if (parent == null) {
            String parentStr = path.substring(0, path.lastIndexOf('/'));
            if (parentStr.isEmpty()) parentStr = "/";
            // For simplicity, create /dev if we're mounting there
            if (parentStr.equals("/dev")) {
                try {
                    parent = resolveDirectory("/dev", root);
                } catch (Exception e) {
                    parent = new DirectoryNode("dev", "root", root);
                    root.addChild(parent);
                }
            } else {
                throw new FileNotFoundException("Parent directory does not exist for mount: " + path);
            }
        }
        parent.addChild(deviceNode);
    }
}
