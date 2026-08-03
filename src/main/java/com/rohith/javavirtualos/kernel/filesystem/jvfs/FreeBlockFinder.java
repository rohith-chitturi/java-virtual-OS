package com.rohith.javavirtualos.kernel.filesystem.jvfs;

import java.util.Optional;

/**
 * Strategy interface to find free blocks in the BitmapManager.
 */
public interface FreeBlockFinder {
    
    /**
     * Finds the next available free block index.
     * @param bitmapManager the bitmap manager to search
     * @return the index of a free block, or empty if none available
     */
    Optional<Integer> findFreeBlock(BitmapManager bitmapManager);
}
