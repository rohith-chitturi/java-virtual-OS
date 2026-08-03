package com.rohith.javavirtualos.kernel.filesystem.jvfs;

import java.nio.ByteBuffer;

/**
 * Represents the SuperBlock in the Java Virtual File System (JVFS).
 * Contains crucial metadata about the filesystem.
 */
public class SuperBlock {
    public static final int MAGIC_NUMBER = 0x4A564653; // "JVFS"
    public static final int VERSION = 1;
    public static final int DEFAULT_BLOCK_SIZE = 4096;
    public static final int SUPERBLOCK_SIZE = 128; // Fixed size in bytes
    
    private int magicNumber;
    private int version;
    private int blockSize;
    private int inodeCount;
    private int totalBlocks;
    private int freeBlocks;
    
    public SuperBlock(int inodeCount, int totalBlocks) {
        this.magicNumber = MAGIC_NUMBER;
        this.version = VERSION;
        this.blockSize = DEFAULT_BLOCK_SIZE;
        this.inodeCount = inodeCount;
        this.totalBlocks = totalBlocks;
        this.freeBlocks = totalBlocks;
    }
    
    // Empty constructor for deserialization
    public SuperBlock() {}

    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(SUPERBLOCK_SIZE);
        buffer.putInt(magicNumber);
        buffer.putInt(version);
        buffer.putInt(blockSize);
        buffer.putInt(inodeCount);
        buffer.putInt(totalBlocks);
        buffer.putInt(freeBlocks);
        return buffer.array();
    }

    public static SuperBlock deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        SuperBlock sb = new SuperBlock();
        sb.magicNumber = buffer.getInt();
        sb.version = buffer.getInt();
        sb.blockSize = buffer.getInt();
        sb.inodeCount = buffer.getInt();
        sb.totalBlocks = buffer.getInt();
        sb.freeBlocks = buffer.getInt();
        
        if (sb.magicNumber != MAGIC_NUMBER) {
            throw new IllegalArgumentException("Invalid magic number: not a JVFS filesystem");
        }
        return sb;
    }

    public int getMagicNumber() { return magicNumber; }
    public int getVersion() { return version; }
    public int getBlockSize() { return blockSize; }
    public int getInodeCount() { return inodeCount; }
    public int getTotalBlocks() { return totalBlocks; }
    public int getFreeBlocks() { return freeBlocks; }

    public void decrementFreeBlocks() {
        if (freeBlocks > 0) freeBlocks--;
    }
    
    public void incrementFreeBlocks() {
        if (freeBlocks < totalBlocks) freeBlocks++;
    }
}
