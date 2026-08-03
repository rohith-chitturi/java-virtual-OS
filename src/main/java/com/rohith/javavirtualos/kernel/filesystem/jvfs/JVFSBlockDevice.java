package com.rohith.javavirtualos.kernel.filesystem.jvfs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

/**
 * Simulates a block-level storage device backed by a host file (e.g. vdisk.img).
 */
public class JVFSBlockDevice {
    private static final Logger LOGGER = Logger.getLogger(JVFSBlockDevice.class.getName());
    
    private final File diskFile;
    private final int blockSize;
    private RandomAccessFile randomAccessFile;

    public JVFSBlockDevice(File diskFile, int blockSize) {
        this.diskFile = diskFile;
        this.blockSize = blockSize;
    }

    public void open() throws IOException {
        if (!diskFile.exists()) {
            LOGGER.info("Creating new virtual disk at " + diskFile.getAbsolutePath());
        }
        this.randomAccessFile = new RandomAccessFile(diskFile, "rw");
    }

    public void close() throws IOException {
        if (randomAccessFile != null) {
            randomAccessFile.close();
            randomAccessFile = null;
        }
    }

    /**
     * Reads a full block from the virtual disk.
     * @param blockNumber the logical block number
     * @return a byte array containing the block data
     */
    public byte[] readBlock(long blockNumber) throws IOException {
        checkOpen();
        byte[] buffer = new byte[blockSize];
        long offset = blockNumber * blockSize;
        randomAccessFile.seek(offset);
        
        int bytesRead = randomAccessFile.read(buffer);
        // If we read less than a full block (e.g. EOF), the rest of the buffer remains 0
        return buffer;
    }

    /**
     * Writes a full block to the virtual disk.
     * @param blockNumber the logical block number
     * @param data the data to write (must be equal to blockSize)
     */
    public void writeBlock(long blockNumber, byte[] data) throws IOException {
        checkOpen();
        if (data.length != blockSize) {
            throw new IllegalArgumentException("Data size (" + data.length + ") does not match block size (" + blockSize + ")");
        }
        long offset = blockNumber * blockSize;
        randomAccessFile.seek(offset);
        randomAccessFile.write(data);
    }
    
    public int getBlockSize() {
        return blockSize;
    }

    private void checkOpen() {
        if (randomAccessFile == null) {
            throw new IllegalStateException("Block device is not open");
        }
    }
}
