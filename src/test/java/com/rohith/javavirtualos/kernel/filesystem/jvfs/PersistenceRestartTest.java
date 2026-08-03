package com.rohith.javavirtualos.kernel.filesystem.jvfs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceRestartTest {

    private File diskFile;
    private final int BLOCK_SIZE = 4096;
    private final int TOTAL_BLOCKS = 100;
    private final int INODE_COUNT = 50;

    @BeforeEach
    void setUp() throws IOException {
        diskFile = File.createTempFile("vdisk_test", ".img");
    }

    @AfterEach
    void tearDown() {
        if (diskFile.exists()) {
            diskFile.delete();
        }
    }

    @Test
    void testPersistenceAcrossRestarts() throws IOException {
        // --- System Boot 1 ---
        JVFSBlockDevice device1 = new JVFSBlockDevice(diskFile, BLOCK_SIZE);
        device1.open();
        
        SuperBlock sb1 = new SuperBlock(INODE_COUNT, TOTAL_BLOCKS);
        byte[] sbData = sb1.serialize();
        byte[] block0 = new byte[BLOCK_SIZE];
        System.arraycopy(sbData, 0, block0, 0, sbData.length);
        device1.writeBlock(0, block0); // Block 0 is SuperBlock

        BitmapManager bm1 = new BitmapManager(TOTAL_BLOCKS);
        BlockAllocator allocator1 = new BlockAllocator(device1, sb1, bm1, new FirstFitFreeBlockFinder(), 1);
        
        // Allocate a few blocks
        int b1 = allocator1.allocateBlock();
        int b2 = allocator1.allocateBlock();
        assertEquals(0, b1);
        assertEquals(1, b2);
        
        device1.close();
        
        // --- System Shutdown ---
        
        // --- System Boot 2 ---
        JVFSBlockDevice device2 = new JVFSBlockDevice(diskFile, BLOCK_SIZE);
        device2.open();
        
        byte[] readBlock0 = device2.readBlock(0);
        SuperBlock sb2 = SuperBlock.deserialize(readBlock0);
        
        assertEquals(SuperBlock.MAGIC_NUMBER, sb2.getMagicNumber());
        assertEquals(TOTAL_BLOCKS, sb2.getTotalBlocks());
        assertEquals(TOTAL_BLOCKS - 2, sb2.getFreeBlocks());
        
        byte[] bitmapData = device2.readBlock(1); // Block 1 is Bitmap
        BitmapManager bm2 = new BitmapManager(bitmapData, TOTAL_BLOCKS);
        
        assertTrue(bm2.isAllocated(0));
        assertTrue(bm2.isAllocated(1));
        assertFalse(bm2.isAllocated(2));
        
        device2.close();
    }
}
