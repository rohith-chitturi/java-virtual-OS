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
    private final InodeLifecycleManager lifecycleManager;
    @SuppressWarnings("unused")
    private final EventBus eventBus;
    
    @SuppressWarnings("unused")
    private SecurityManager securityManager;

    public FileSystemManager() {
        this.root = new DirectoryNode("root");
        this.pathResolver = new PathResolver(this.root);
        this.validator = new FileSystemValidator();
        this.lifecycleManager = new InodeLifecycleManager();
        this.lifecycleManager.incrementLinkCount(this.root);
        this.eventBus = null; // Stub until Events module is fully built
    }

    public InodeLifecycleManager getLifecycleManager() {
        return lifecycleManager;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
        this.validator.setSecurityManager(securityManager);
    }

    public DirectoryNode getRoot() {
        return root;
    }

    public Inode resolvePath(String path, DirectoryNode currentDir) throws FileSystemException {
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

        DirectoryNode newDir = new DirectoryNode(currentUser.getUsername());
        parent.addChild(name, newDir);
        lifecycleManager.incrementLinkCount(newDir);
    }

    public void createFile(String path, DirectoryNode currentDir, User currentUser) throws FileSystemException {
        DirectoryNode parent = pathResolver.resolveParentDirectory(path, currentDir);
        if (parent == null) throw new FileNotFoundException("Parent directory does not exist");
        
        String name = pathResolver.extractName(path);
        validator.validateCreation(parent, name, currentUser);

        FileNode newFile = new FileNode(currentUser.getUsername());
        parent.addChild(name, newFile);
        lifecycleManager.incrementLinkCount(newFile);
    }

    public void createHardLink(String existingPath, String newPath, DirectoryNode currentDir, User currentUser) throws FileSystemException {
        Inode target = pathResolver.resolvePath(existingPath, currentDir);
        if (target == null) throw new FileNotFoundException(existingPath);
        
        if (target.getType() == com.rohith.javavirtualos.filesystem.model.FileType.DIRECTORY) {
            throw new FileSystemException("Hard links not allowed for directories");
        }

        DirectoryNode parent = pathResolver.resolveParentDirectory(newPath, currentDir);
        if (parent == null) throw new FileNotFoundException("Parent directory does not exist for new link");
        
        String linkName = pathResolver.extractName(newPath);
        validator.validateCreation(parent, linkName, currentUser);

        parent.addChild(linkName, target);
        lifecycleManager.incrementLinkCount(target);
    }

    public void createSymlink(String targetPath, String linkPath, DirectoryNode currentDir, User currentUser) throws FileSystemException {
        DirectoryNode parent = pathResolver.resolveParentDirectory(linkPath, currentDir);
        if (parent == null) throw new FileNotFoundException("Parent directory does not exist for new link");

        String linkName = pathResolver.extractName(linkPath);
        
        if (parent.getChild(linkName) != null) {
            throw new FileSystemException("File already exists: " + linkPath);
        }
        
        validator.validateCreation(parent, linkName, currentUser);

        com.rohith.javavirtualos.filesystem.model.SymlinkNode symlink = 
            new com.rohith.javavirtualos.filesystem.model.SymlinkNode(currentUser.getUsername(), targetPath);
        parent.addChild(linkName, symlink);
        lifecycleManager.incrementLinkCount(symlink);
    }

    public void remove(String path, DirectoryNode currentDir, String currentActivePath, boolean isDirectoryCommand, User currentUser) throws FileSystemException {
        DirectoryNode parent = pathResolver.resolveParentDirectory(path, currentDir);
        if (parent == null) throw new FileNotFoundException("Parent directory does not exist");
        String name = pathResolver.extractName(path);
        Inode target = parent.getChild(name);
        
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
            // resolve absolute path of target for string matching
            String absoluteTarget = path.startsWith("/") ? path : (currentActivePath.equals("/") ? "/" + path : currentActivePath + "/" + path);
            validator.validateDeletion(targetDir, parent, absoluteTarget, currentActivePath, currentUser);
        } else {
            validator.validateWrite(target, currentUser);
        }

        parent.removeChild(name);
        lifecycleManager.decrementLinkCount(target);
    }

    public String readlink(String path, DirectoryNode currentDir, User currentUser) throws FileSystemException {
        DirectoryNode parent = pathResolver.resolveParentDirectory(path, currentDir);
        if (parent == null) throw new FileNotFoundException("Parent directory does not exist");
        String name = pathResolver.extractName(path);
        Inode target = parent.getChild(name);
        
        if (target == null) throw new FileNotFoundException(path);
        if (!(target instanceof com.rohith.javavirtualos.filesystem.model.SymlinkNode)) {
            throw new FileSystemException(path + " is not a symbolic link");
        }
        
        validator.validateRead(target, currentUser);
        return ((com.rohith.javavirtualos.filesystem.model.SymlinkNode) target).getTargetPath();
    }

    public void validateReadAccess(Inode target, User currentUser) throws FileSystemException {
        validator.validateRead(target, currentUser);
    }
    
    public void validateExecuteAccess(Inode target, User currentUser) throws FileSystemException {
        validator.validateExecute(target, currentUser);
    }
    
    public void validateWriteAccess(Inode target, User currentUser) throws FileSystemException {
        validator.validateWrite(target, currentUser);
    }
    
    public void mountDevice(String path, DeviceNode deviceNode) throws FileSystemException {
        DirectoryNode parent = pathResolver.resolveParentDirectory(path, root);
        String name = pathResolver.extractName(path);
        if (parent == null) {
            String parentStr = path.substring(0, path.lastIndexOf('/'));
            if (parentStr.isEmpty()) parentStr = "/";
            // For simplicity, create /dev if we're mounting there
            if (parentStr.equals("/dev")) {
                try {
                    parent = resolveDirectory("/dev", root);
                } catch (Exception e) {
                    parent = new DirectoryNode("root");
                    root.addChild("dev", parent);
                }
            } else {
                throw new FileNotFoundException("Parent directory does not exist for mount: " + path);
            }
        }
        parent.addChild(name, deviceNode);
        lifecycleManager.incrementLinkCount(deviceNode);
    }
}
