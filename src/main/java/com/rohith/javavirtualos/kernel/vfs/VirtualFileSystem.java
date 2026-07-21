package com.rohith.javavirtualos.kernel.vfs;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.VfsEvent.*;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public class VirtualFileSystem {
    private final MountPoint rootMount;
    private final PathResolver resolver;
    private final KernelEventBus eventBus;
    private final FileSystemStatistics stats;

    public VirtualFileSystem(KernelEventBus eventBus, FileSystemStatistics stats) {
        INode rootInode = new INode(FileType.DIRECTORY, 0, EnumSet.of(Permission.READ, Permission.WRITE, Permission.EXECUTE));
        VfsDirectory rootDir = new VfsDirectory("/", rootInode);
        this.rootMount = new MountPoint(Path.of("/"), rootDir);
        this.resolver = new PathResolver(this.rootMount);
        this.eventBus = eventBus;
        this.stats = stats;
        stats.recordDirectoryCreated();
    }

    public MountPoint getRootMount() { return rootMount; }
    public PathResolver getResolver() { return resolver; }
    public FileSystemStatistics getStats() { return stats; }

    public VfsFile createFile(Path path, VfsDirectory cwd, int ownerUid, Set<Permission> permissions) {
        Optional<VfsDirectory> parentOpt = resolver.resolveParent(path, cwd);
        if (parentOpt.isEmpty()) throw new IllegalArgumentException("Parent directory does not exist");
        
        VfsDirectory parent = parentOpt.get();
        if (parent.getChild(path.getName()).isPresent()) {
            throw new IllegalArgumentException("File already exists");
        }
        
        INode inode = new INode(FileType.FILE, ownerUid, permissions);
        VfsFile file = new VfsFile(path.getName(), inode);
        parent.addChild(file);
        
        stats.recordFileCreated();
        eventBus.publish(new FileCreatedEvent(path));
        return file;
    }

    public VfsDirectory createDirectory(Path path, VfsDirectory cwd, int ownerUid, Set<Permission> permissions) {
        Optional<VfsDirectory> parentOpt = resolver.resolveParent(path, cwd);
        if (parentOpt.isEmpty()) throw new IllegalArgumentException("Parent directory does not exist");
        
        VfsDirectory parent = parentOpt.get();
        if (parent.getChild(path.getName()).isPresent()) {
            throw new IllegalArgumentException("Directory already exists");
        }
        
        INode inode = new INode(FileType.DIRECTORY, ownerUid, permissions);
        VfsDirectory dir = new VfsDirectory(path.getName(), inode);
        parent.addChild(dir);
        
        stats.recordDirectoryCreated();
        eventBus.publish(new DirectoryCreatedEvent(path));
        return dir;
    }

    public boolean delete(Path path, VfsDirectory cwd) {
        if (path.toString().equals("/")) throw new IllegalArgumentException("Cannot delete root");
        
        Optional<VfsNode> nodeOpt = resolver.resolve(path, cwd);
        if (nodeOpt.isEmpty()) return false;
        
        VfsNode node = nodeOpt.get();
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
