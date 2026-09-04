package com.rohith.javavirtualos.filesystem.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract base class for any element in the virtual file system.
 * It is decoupled from the tree structure (no parent, no name).
 */
public abstract class Inode {
    
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    protected final long inodeId;
    protected final FileMetadata metadata;
    protected int linkCount;
    protected int openReferenceCount;

    public Inode(String owner) {
        this.inodeId = ID_GENERATOR.getAndIncrement();
        this.metadata = new FileMetadata(owner);
        this.linkCount = 0;
        this.openReferenceCount = 0;
    }

    public Inode(long inodeId, String owner) {
        this.inodeId = inodeId;
        this.metadata = new FileMetadata(owner);
        this.linkCount = 0;
        this.openReferenceCount = 0;
    }

    public long getInodeId() {
        return inodeId;
    }

    public FileMetadata getMetadata() {
        return metadata;
    }

    public int getLinkCount() {
        return linkCount;
    }

    public void incrementLinkCount() {
        this.linkCount++;
    }

    public void decrementLinkCount() {
        if (this.linkCount > 0) {
            this.linkCount--;
        }
    }

    public int getOpenReferenceCount() {
        return openReferenceCount;
    }

    public void incrementOpenReferenceCount() {
        this.openReferenceCount++;
    }

    public void decrementOpenReferenceCount() {
        if (this.openReferenceCount > 0) {
            this.openReferenceCount--;
        }
    }

    public boolean canBeReclaimed() {
        return linkCount == 0 && openReferenceCount == 0;
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
