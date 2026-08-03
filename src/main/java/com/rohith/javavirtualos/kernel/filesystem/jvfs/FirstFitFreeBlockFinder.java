package com.rohith.javavirtualos.kernel.filesystem.jvfs;

import java.util.Optional;

/**
 * Basic First-Fit implementation of FreeBlockFinder.
 */
public class FirstFitFreeBlockFinder implements FreeBlockFinder {
    
    @Override
    public Optional<Integer> findFreeBlock(BitmapManager bitmapManager) {
        for (int i = 0; i < bitmapManager.getTotalBlocks(); i++) {
            if (!bitmapManager.isAllocated(i)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }
}
