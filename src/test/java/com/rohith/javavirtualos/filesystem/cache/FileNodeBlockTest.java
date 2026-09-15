package com.rohith.javavirtualos.filesystem.cache;

import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.storage.DefaultFileStorage;
import com.rohith.javavirtualos.kernel.filesystem.jvfs.BlockAllocator;
import com.rohith.javavirtualos.kernel.filesystem.jvfs.JVFSBlockDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class FileNodeBlockTest {

    private DefaultFileStorage storage;
    private FileNode fileNode;

    @BeforeEach
    public void setUp() {
        // Mock dependencies or use simple implementations
        JVFSBlockDevice device = new JVFSBlockDevice(new java.io.File("dummy.img"), 4096) {
            private final byte[][] disk = new byte[1000][4096];
            @Override
            public void writeBlock(long blockIndex, byte[] data) {
                System.arraycopy(data, 0, disk[(int)blockIndex], 0, Math.min(4096, data.length));
            }
            @Override
            public byte[] readBlock(long blockIndex) {
                return Arrays.copyOf(disk[(int)blockIndex], 4096);
            }
        };

        com.rohith.javavirtualos.kernel.filesystem.jvfs.SuperBlock sb = new com.rohith.javavirtualos.kernel.filesystem.jvfs.SuperBlock(100, 100);
        com.rohith.javavirtualos.kernel.filesystem.jvfs.BitmapManager bm = new com.rohith.javavirtualos.kernel.filesystem.jvfs.BitmapManager(100);
        com.rohith.javavirtualos.kernel.filesystem.jvfs.FreeBlockFinder finder = new com.rohith.javavirtualos.kernel.filesystem.jvfs.FirstFitFreeBlockFinder();
        
        BlockAllocator allocator = new BlockAllocator(device, sb, bm, finder, 1) {
            int nextBlock = 0;
            @Override
            public int allocateBlock() {
                return nextBlock++;
            }
            @Override
            public void freeBlock(int blockIndex) { }
        };

        storage = new DefaultFileStorage(allocator, device, 10);
        fileNode = new FileNode("testUser");
    }

    @Test
    public void testPartialWrite() throws Exception {
        byte[] data = "HelloWorld".getBytes();
        storage.write(fileNode, 100, data);
        
        byte[] readData = storage.read(fileNode, 100, 10);
        assertArrayEquals(data, readData);
    }

    @Test
    public void testPageBoundaryWrite() throws Exception {
        byte[] data = "BoundaryData12345".getBytes();
        int offset = DefaultFileStorage.PAGE_SIZE - 10;
        
        storage.write(fileNode, offset, data);
        
        byte[] readData = storage.read(fileNode, offset, data.length);
        assertArrayEquals(data, readData);
    }

    @Test
    public void testCrossPageWrite() throws Exception {
        byte[] data = "CrossPageBoundaryCheck!".getBytes();
        int offset = DefaultFileStorage.PAGE_SIZE - 5; // 5 bytes in page 0, rest in page 1
        
        storage.write(fileNode, offset, data);
        
        byte[] readData = storage.read(fileNode, offset, data.length);
        assertArrayEquals(data, readData);
    }

    @Test
    public void testMultiPageWrite() throws Exception {
        byte[] data = new byte[10000];
        Arrays.fill(data, (byte) 'A');
        
        storage.write(fileNode, 100, data);
        
        byte[] readData = storage.read(fileNode, 100, data.length);
        assertArrayEquals(data, readData);
    }

    @Test
    public void testReadAfterWrite() throws Exception {
        storage.write(fileNode, 0, "Test".getBytes());
        byte[] readData = storage.read(fileNode, 0, 4);
        assertEquals("Test", new String(readData));
    }

    @Test
    public void testFileSizeUpdate() throws Exception {
        assertEquals(0, fileNode.calculateSize());
        storage.write(fileNode, 50, "Test".getBytes());
        assertEquals(54, fileNode.calculateSize());
    }

    @Test
    public void testZeroFillGap() throws Exception {
        storage.write(fileNode, 4090, "Test".getBytes());
        
        // Reading the gap should return zeroes
        byte[] gap = storage.read(fileNode, 0, 4090);
        for (byte b : gap) {
            assertEquals(0, b);
        }
        
        byte[] data = storage.read(fileNode, 4090, 4);
        assertEquals("Test", new String(data));
    }
}
