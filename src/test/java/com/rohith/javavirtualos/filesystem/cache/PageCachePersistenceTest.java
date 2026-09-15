package com.rohith.javavirtualos.filesystem.cache;

import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.storage.DefaultFileStorage;
import com.rohith.javavirtualos.kernel.filesystem.jvfs.BlockAllocator;
import com.rohith.javavirtualos.kernel.filesystem.jvfs.JVFSBlockDevice;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class PageCachePersistenceTest {

    @Test
    public void testPersistenceAcrossMounts() throws Exception {
        // Shared backing store simulating disk
        byte[][] sharedDisk = new byte[100][4096];

        // Session 1: Mount, write, flush, shutdown
        JVFSBlockDevice device1 = new JVFSBlockDevice(new java.io.File("dummy.img"), 4096) {
            @Override
            public void writeBlock(long blockIndex, byte[] data) {
                System.arraycopy(data, 0, sharedDisk[(int)blockIndex], 0, Math.min(4096, data.length));
            }
            @Override
            public byte[] readBlock(long blockIndex) {
                return Arrays.copyOf(sharedDisk[(int)blockIndex], 4096);
            }
        };
        com.rohith.javavirtualos.kernel.filesystem.jvfs.SuperBlock sb1 = new com.rohith.javavirtualos.kernel.filesystem.jvfs.SuperBlock(100, 100);
        com.rohith.javavirtualos.kernel.filesystem.jvfs.BitmapManager bm1 = new com.rohith.javavirtualos.kernel.filesystem.jvfs.BitmapManager(100);
        com.rohith.javavirtualos.kernel.filesystem.jvfs.FreeBlockFinder finder1 = new com.rohith.javavirtualos.kernel.filesystem.jvfs.FirstFitFreeBlockFinder();
        
        BlockAllocator allocator1 = new BlockAllocator(device1, sb1, bm1, finder1, 1) {
            int nextBlock = 0;
            @Override
            public int allocateBlock() { return nextBlock++; }
            @Override
            public void freeBlock(int blockIndex) { }
        };

        DefaultFileStorage storage1 = new DefaultFileStorage(allocator1, device1, 10);
        FileNode fileNode = new FileNode(42L, "testUser");

        // write
        byte[] data = "PersistentData123".getBytes();
        storage1.write(fileNode, 50, data);

        // flush
        storage1.flush(fileNode);

        // shutdown
        storage1.getPageCache().clear(); // Simulating memory loss on shutdown

        // Session 2: Remount
        JVFSBlockDevice device2 = new JVFSBlockDevice(new java.io.File("dummy.img"), 4096) {
            @Override
            public void writeBlock(long blockIndex, byte[] data) {
                System.arraycopy(data, 0, sharedDisk[(int)blockIndex], 0, Math.min(4096, data.length));
            }
            @Override
            public byte[] readBlock(long blockIndex) {
                return Arrays.copyOf(sharedDisk[(int)blockIndex], 4096);
            }
        };
        com.rohith.javavirtualos.kernel.filesystem.jvfs.SuperBlock sb2 = new com.rohith.javavirtualos.kernel.filesystem.jvfs.SuperBlock(100, 100);
        com.rohith.javavirtualos.kernel.filesystem.jvfs.BitmapManager bm2 = new com.rohith.javavirtualos.kernel.filesystem.jvfs.BitmapManager(100);
        com.rohith.javavirtualos.kernel.filesystem.jvfs.FreeBlockFinder finder2 = new com.rohith.javavirtualos.kernel.filesystem.jvfs.FirstFitFreeBlockFinder();
        
        BlockAllocator allocator2 = new BlockAllocator(device2, sb2, bm2, finder2, 1) {
            int nextBlock = 1; // Pick up where left off
            @Override
            public int allocateBlock() { return nextBlock++; }
            @Override
            public void freeBlock(int blockIndex) { }
        };

        DefaultFileStorage storage2 = new DefaultFileStorage(allocator2, device2, 10);
        // fileNode metadata (size, block map) is persistent in JVFS inode table, but here we just reuse the object
        
        // verifyBytes
        byte[] readData = storage2.read(fileNode, 50, data.length);
        assertArrayEquals(data, readData);
    }
}
