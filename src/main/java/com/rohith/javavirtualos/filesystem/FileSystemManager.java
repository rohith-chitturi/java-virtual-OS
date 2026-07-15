package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.events.EventBus;
import com.rohith.javavirtualos.events.FileSystemEvent;
import com.rohith.javavirtualos.exceptions.FileNotFoundException;
import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.model.Inode;

/**
 * Central orchestrator for all in-memory file system operations.
 */
public class FileSystemManager {

    private final DirectoryNode root;
    private final PathResolver pathResolver;
    private final FileSystemValidator validator;
    private final EventBus eventBus; // We'll assume this is passed in later, or instantiated here for now.

    public FileSystemManager() {
        this.root = new DirectoryNode("", "root", null);
        this.pathResolver = new PathResolver(this.root);
        this.validator = new FileSystemValidator();
        this.eventBus = null; // Stub until Events module is fully built
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

    public void createDirectory(String path, DirectoryNode currentDir, String owner) throws FileSystemException {
        DirectoryNode parent = pathResolver.resolveParentDirectory(path, currentDir);
        if (parent == null) throw new FileNotFoundException("Parent directory does not exist");
        
        String name = pathResolver.extractName(path);
        validator.validateCreation(parent, name);

        DirectoryNode newDir = new DirectoryNode(name, owner, parent);
        parent.addChild(newDir);
        
        // Emitting event (when EventBus is ready)
        // eventBus.publish(new FileSystemEvent("DIRECTORY_CREATED", newDir.getAbsolutePath()));
    }

    public void createFile(String path, DirectoryNode currentDir, String owner) throws FileSystemException {
        DirectoryNode parent = pathResolver.resolveParentDirectory(path, currentDir);
        if (parent == null) throw new FileNotFoundException("Parent directory does not exist");
        
        String name = pathResolver.extractName(path);
        validator.validateCreation(parent, name);

        FileNode newFile = new FileNode(name, owner, parent);
        parent.addChild(newFile);
    }

    public void remove(String path, DirectoryNode currentDir, boolean isDirectoryCommand) throws FileSystemException {
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
            validator.validateDeletion(targetDir, currentDir);
        }

        target.getParent().removeChild(target.getName());
    }
}
