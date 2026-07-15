package com.rohith.javavirtualos.filesystem.model;

/**
 * Abstract base class for any element in the virtual file system (Composite Pattern Component).
 */
public abstract class Inode {
    
    protected final FileMetadata metadata;
    protected DirectoryNode parent;

    public Inode(String name, String owner, DirectoryNode parent) {
        this.metadata = new FileMetadata(name, owner);
        this.parent = parent;
    }

    public FileMetadata getMetadata() {
        return metadata;
    }

    public DirectoryNode getParent() {
        return parent;
    }

    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    public String getName() {
        return metadata.getName();
    }
    
    public void setName(String name) {
        metadata.setName(name);
    }

    /**
     * Absolute path construction.
     */
    public String getAbsolutePath() {
        if (parent == null) {
            return "/"; // Root directory
        }
        String parentPath = parent.getAbsolutePath();
        if (parentPath.equals("/")) {
            return "/" + getName();
        }
        return parentPath + "/" + getName();
    }

    /**
     * Required implementation to distinguish the node type.
     */
    public abstract FileType getType();

    /**
     * Calculate size dynamically (useful for directories).
     */
    public abstract long calculateSize();
}
