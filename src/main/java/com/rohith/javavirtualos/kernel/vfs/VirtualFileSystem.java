package com.rohith.javavirtualos.kernel.vfs;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.security.PermissionBits;
import java.util.Optional;

public class VirtualFileSystem {
    private final MountPoint rootMount;
    private final PathResolver resolver;
    private final KernelEventBus eventBus;
    private final FileSystemStatistics stats;
    private final com.rohith.javavirtualos.kernel.security.SecurityManager securityManager;

    public VirtualFileSystem(KernelEventBus eventBus, FileSystemStatistics stats, com.rohith.javavirtualos.kernel.security.SecurityManager securityManager) {
        INode rootInode = new INode(FileType.DIRECTORY, 0, 0, PermissionBits.fromOctal("755"));
        VfsDirectory rootDir = new VfsDirectory("/", rootInode);
        this.rootMount = new MountPoint(Path.of("/"), rootDir);
        this.resolver = new PathResolver(this.rootMount);
        this.eventBus = eventBus;
        this.stats = stats;
        this.securityManager = securityManager;
        stats.recordDirectoryCreated();
    }

    public MountPoint getRootMount() { return rootMount; }
    public PathResolver getResolver() { return resolver; }
    public FileSystemStatistics getStats() { return stats; }

    public VfsFile createFile(com.rohith.javavirtualos.kernel.security.User user, Path path, VfsDirectory cwd, PermissionBits permissions) {
        Optional<VfsDirectory> parentOpt = resolver.resolveParent(path, cwd);
        if (parentOpt.isEmpty()) throw new IllegalArgumentException("Parent directory does not exist");
        
        VfsDirectory parent = parentOpt.get();
        if (securityManager != null && !securityManager.canAccess(user, parent.getINode(), com.rohith.javavirtualos.kernel.security.AccessMode.WRITE)) {
            eventBus.publish(new com.rohith.javavirtualos.kernel.events.SecurityEvent.PermissionDeniedEvent(user.getUsername(), parent.getName()));
            throw new SecurityException("Permission denied to create file in " + parent.getName());
        }
        
        if (parent.getChild(path.getName()).isPresent()) {
            throw new IllegalArgumentException("File already exists");
        }
        
        INode inode = new INode(FileType.FILE, user.getUid(), user.getPrimaryGroupId(), permissions);
        VfsFile file = new VfsFile(path.getName(), inode);
        parent.addChild(file);
        
        stats.recordFileCreated();
        eventBus.publish(new FileCreatedEvent(path));
        return file;
    }

    public VfsDirectory createDirectory(com.rohith.javavirtualos.kernel.security.User user, Path path, VfsDirectory cwd, PermissionBits permissions) {
        Optional<VfsDirectory> parentOpt = resolver.resolveParent(path, cwd);
        if (parentOpt.isEmpty()) throw new IllegalArgumentException("Parent directory does not exist");
        
        VfsDirectory parent = parentOpt.get();
        if (securityManager != null && !securityManager.canAccess(user, parent.getINode(), com.rohith.javavirtualos.kernel.security.AccessMode.WRITE)) {
            eventBus.publish(new com.rohith.javavirtualos.kernel.events.SecurityEvent.PermissionDeniedEvent(user.getUsername(), parent.getName()));
            throw new SecurityException("Permission denied to create directory in " + parent.getName());
        }
        
        if (parent.getChild(path.getName()).isPresent()) {
            throw new IllegalArgumentException("Directory already exists");
        }
        
        INode inode = new INode(FileType.DIRECTORY, user.getUid(), user.getPrimaryGroupId(), permissions);
        VfsDirectory dir = new VfsDirectory(path.getName(), inode);
        parent.addChild(dir);
        
        stats.recordDirectoryCreated();
        eventBus.publish(new DirectoryCreatedEvent(path));
        return dir;
    }

    public boolean delete(com.rohith.javavirtualos.kernel.security.User user, Path path, VfsDirectory cwd) {
        if (path.toString().equals("/")) throw new IllegalArgumentException("Cannot delete root");
        
        Optional<VfsNode> nodeOpt = resolver.resolve(path, cwd);
        if (nodeOpt.isEmpty()) return false;
        
        VfsNode node = nodeOpt.get();
        VfsDirectory parent = node.getParent();
        
        if (securityManager != null && !securityManager.canAccess(user, parent.getINode(), com.rohith.javavirtualos.kernel.security.AccessMode.WRITE)) {
            eventBus.publish(new com.rohith.javavirtualos.kernel.events.SecurityEvent.PermissionDeniedEvent(user.getUsername(), parent.getName()));
            throw new SecurityException("Permission denied to delete in " + parent.getName());
        }
        
        if (node.isDirectory() && !((VfsDirectory)node).getChildren().isEmpty()) {
            throw new IllegalArgumentException("Directory not empty");
        }
        
        node.getParent().removeChild(node.getName());
        stats.recordDelete();
        
        if (node.isDirectory()) {
            eventBus.publish(new DirectoryDeletedEvent(path));
        } else {
            eventBus.publish(new FileDeletedEvent(path));
        }
        return true;
    }
}
