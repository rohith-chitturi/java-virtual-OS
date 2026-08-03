package com.rohith.javavirtualos.kernel.filesystem.jvfs;

import java.io.IOException;

/**
 * High-level allocator that manages the allocation and freeing of disk blocks.
 * Bridges the BitmapManager with the physical JVFSBlockDevice.
 */
public class BlockAllocator {
    private final JVFSBlockDevice device;
    private final SuperBlock superBlock;
    private final BitmapManager bitmapManager;
    private final FreeBlockFinder finder;

    // The logical block where the bitmap is stored on disk
    private final long bitmapBlockOffset; 

    public BlockAllocator(JVFSBlockDevice device, SuperBlock superBlock, BitmapManager bitmapManager, FreeBlockFinder finder, long bitmapBlockOffset) {
        this.device = device;
        this.superBlock = superBlock;
        this.bitmapManager = bitmapManager;
        this.finder = finder;
        this.bitmapBlockOffset = bitmapBlockOffset;
    }

    /**
     * Allocates the next available free block.
     * @return the allocated block number, or -1 if no blocks are free.
     * @throws IOException if persisting the bitmap fails
     */
    public synchronized int allocateBlock() throws IOException {
        Integer freeBlock = finder.findFreeBlock(bitmapManager).orElse(null);
        if (freeBlock == null) {
            return -1; // Out of space
        }

        bitmapManager.setAllocated(freeBlock);
        superBlock.decrementFreeBlocks();
        
        persistBitmap();
        return freeBlock;
    }

    /**
     * Frees an allocated block.
     * @param blockNumber the block number to free
     * @throws IOException if persisting the bitmap fails
     */
    public synchronized void freeBlock(int blockNumber) throws IOException {
        if (!bitmapManager.isAllocated(blockNumber)) {
            return; // Already free
        }
        
        bitmapManager.setFree(blockNumber);
        superBlock.incrementFreeBlocks();
        
        // Zero-out the block on disk for security
        byte[] zeroBlock = new byte[device.getBlockSize()];
        device.writeBlock(blockNumber, zeroBlock);

        persistBitmap();
    }

    private void persistBitmap() throws IOException {
        // Write bitmap back to disk. For simplicity, assume it fits in one block for now.
        // If it spans multiple blocks, this logic needs to chunk it.
        byte[] data = bitmapManager.getBitmapData();
        byte[] blockData = new byte[device.getBlockSize()];
        System.arraycopy(data, 0, blockData, 0, Math.min(data.length, blockData.length));
        
        device.writeBlock(bitmapBlockOffset, blockData);
    }
}
