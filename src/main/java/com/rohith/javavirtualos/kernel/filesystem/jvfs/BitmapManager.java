package com.rohith.javavirtualos.kernel.filesystem.jvfs;

/**
 * Manages a bitmap of free/allocated blocks.
 * A set bit (1) implies the block is allocated, and a cleared bit (0) implies the block is free.
 */
public class BitmapManager {
    private final byte[] bitmap;
    private final int totalBlocks;

    public BitmapManager(int totalBlocks) {
        this.totalBlocks = totalBlocks;
        // 1 byte = 8 blocks
        int numBytes = (int) Math.ceil(totalBlocks / 8.0);
        this.bitmap = new byte[numBytes];
    }

    public BitmapManager(byte[] existingBitmap, int totalBlocks) {
        this.bitmap = existingBitmap;
        this.totalBlocks = totalBlocks;
    }

    public void setAllocated(int blockIndex) {
        if (blockIndex < 0 || blockIndex >= totalBlocks) throw new IllegalArgumentException("Invalid block index");
        int byteIndex = blockIndex / 8;
        int bitOffset = blockIndex % 8;
        bitmap[byteIndex] |= (1 << bitOffset);
    }

    public void setFree(int blockIndex) {
        if (blockIndex < 0 || blockIndex >= totalBlocks) throw new IllegalArgumentException("Invalid block index");
        int byteIndex = blockIndex / 8;
        int bitOffset = blockIndex % 8;
        bitmap[byteIndex] &= ~(1 << bitOffset);
    }

    public boolean isAllocated(int blockIndex) {
        if (blockIndex < 0 || blockIndex >= totalBlocks) return false;
        int byteIndex = blockIndex / 8;
        int bitOffset = blockIndex % 8;
        return (bitmap[byteIndex] & (1 << bitOffset)) != 0;
    }

    public byte[] getBitmapData() {
        return bitmap;
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }
}
