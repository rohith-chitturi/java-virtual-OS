package com.rohith.javavirtualos.kernel.filesystem.jvfs;

import java.io.IOException;
import java.util.Optional;

/**
 * Manages the Inode Table stored on the block device.
 */
public class InodeManager {
    
    private final JVFSBlockDevice device;
    private final SuperBlock superBlock;
    
    // Block offset where the inode table begins
    private final long inodeTableStartBlock;
    
    public InodeManager(JVFSBlockDevice device, SuperBlock superBlock, long inodeTableStartBlock) {
        this.device = device;
        this.superBlock = superBlock;
        this.inodeTableStartBlock = inodeTableStartBlock;
    }

    /**
     * Reads an inode from the disk based on its inode number.
     */
    public Optional<Inode> getInode(int inodeNumber) throws IOException {
        if (inodeNumber < 0 || inodeNumber >= superBlock.getInodeCount()) {
            return Optional.empty();
        }
        
        // Calculate which block contains the inode
        int inodesPerBlock = device.getBlockSize() / Inode.INODE_SIZE;
        long blockOffset = inodeNumber / inodesPerBlock;
        int inodeIndexInBlock = inodeNumber % inodesPerBlock;
        
        long actualBlockNumber = inodeTableStartBlock + blockOffset;
        byte[] blockData = device.readBlock(actualBlockNumber);
        
        int byteOffset = inodeIndexInBlock * Inode.INODE_SIZE;
        Inode inode = Inode.deserialize(blockData, byteOffset);
        
        // If inodeNumber is 0 but the read inode has a different number, it might be uninitialized
        if (inode.getInodeNumber() != inodeNumber && inode.getFileType() == 0) {
            return Optional.empty(); 
        }
        
        return Optional.of(inode);
    }

    /**
     * Writes an inode to the disk.
     */
    public void saveInode(Inode inode) throws IOException {
        int inodeNumber = inode.getInodeNumber();
        if (inodeNumber < 0 || inodeNumber >= superBlock.getInodeCount()) {
            throw new IllegalArgumentException("Invalid inode number: " + inodeNumber);
        }
        
        int inodesPerBlock = device.getBlockSize() / Inode.INODE_SIZE;
        long blockOffset = inodeNumber / inodesPerBlock;
        int inodeIndexInBlock = inodeNumber % inodesPerBlock;
        
        long actualBlockNumber = inodeTableStartBlock + blockOffset;
        byte[] blockData = device.readBlock(actualBlockNumber);
        
        byte[] serializedInode = inode.serialize();
        int byteOffset = inodeIndexInBlock * Inode.INODE_SIZE;
        System.arraycopy(serializedInode, 0, blockData, byteOffset, Inode.INODE_SIZE);
        
        device.writeBlock(actualBlockNumber, blockData);
    }
}
