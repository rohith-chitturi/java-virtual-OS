package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.filesystem.model.Inode;

/**
 * Centralizes lifecycle management for Inodes, tracking links and open references.
 * Automatically triggers reclamation when both counts reach zero.
 */
public class InodeLifecycleManager {

    public void incrementLinkCount(Inode inode) {
        inode.incrementLinkCount();
    }

    public void decrementLinkCount(Inode inode) {
        inode.decrementLinkCount();
        tryReclaim(inode);
    }

    public void incrementOpenReference(Inode inode) {
        inode.incrementOpenReferenceCount();
    }

    public void decrementOpenReference(Inode inode) {
        inode.decrementOpenReferenceCount();
        tryReclaim(inode);
    }

    public void tryReclaim(Inode inode) {
        if (inode.canBeReclaimed()) {
            // Hook for future JVFS on-disk block reclamation.
            // For Phase 2 in-memory objects, the JVM GC will reclaim the object natively.
            // When journaling/VFS blocks are implemented, we call blockAllocator.free(inode) here.
        }
    }
}
