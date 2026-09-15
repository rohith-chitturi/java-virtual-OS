package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.kernel.process.descriptor.FileDescriptorTable;
import com.rohith.javavirtualos.kernel.process.descriptor.OpenFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpenReferenceLifecycleTest {
    private FileSystemManager manager;
    private User testUser;
    private DirectoryNode root;
    private FileDescriptorTable fdTable;

    @BeforeEach
    public void setUp() throws FileSystemException {
        manager = new FileSystemManager();
        manager.setFileStorage(new com.rohith.javavirtualos.filesystem.storage.FileStorage() {
            private final java.util.Map<Long, byte[]> mem = new java.util.HashMap<>();
            @Override
            public byte[] read(Inode inode, long offset, int length) throws Exception {
                byte[] data = mem.getOrDefault(inode.getInodeId(), new byte[0]);
                if (offset >= data.length) return new byte[0];
                int len = Math.min(length, data.length - (int)offset);
                byte[] res = new byte[len];
                System.arraycopy(data, (int)offset, res, 0, len);
                return res;
            }
            @Override
            public void write(Inode inode, long offset, byte[] data) throws Exception {
                byte[] exist = mem.getOrDefault(inode.getInodeId(), new byte[0]);
                byte[] newArr = new byte[Math.max(exist.length, (int)offset + data.length)];
                System.arraycopy(exist, 0, newArr, 0, exist.length);
                System.arraycopy(data, 0, newArr, (int)offset, data.length);
                mem.put(inode.getInodeId(), newArr);
                inode.getMetadata().setSize(newArr.length);
            }
            @Override
            public void flushAll() throws Exception {}
        });
        testUser = new User("testuser", "password");
        root = manager.getRoot();
        fdTable = new FileDescriptorTable();
        manager.createDirectory("home", root, testUser);
    }

    @Test
    public void testOpenIncrementsReferenceCount() throws FileSystemException {
        manager.createFile("/home/a.txt", root, testUser);
        Inode file = manager.resolvePath("/home/a.txt", root, new com.rohith.javavirtualos.kernel.User("root", null));

        OpenFile openFile = new OpenFile(file, manager.getLifecycleManager());
        manager.getLifecycleManager().incrementOpenReference(file);
        
        assertEquals(1, file.getOpenReferenceCount());
    }

    @Test
    public void testUnlinkDoesNotReclaimOpenInode() throws FileSystemException {
        manager.createFile("/home/a.txt", root, testUser);
        Inode file = manager.resolvePath("/home/a.txt", root, new com.rohith.javavirtualos.kernel.User("root", null));

        OpenFile openFile = new OpenFile(file, manager.getLifecycleManager());
        manager.getLifecycleManager().incrementOpenReference(file);

        manager.remove("/home/a.txt", root, "/", false, testUser);

        assertEquals(0, file.getLinkCount());
        assertEquals(1, file.getOpenReferenceCount());
        assertFalse(file.canBeReclaimed());
    }

    @Test
    public void testCloseDecrementsReferenceCount() throws FileSystemException {
        manager.createFile("/home/a.txt", root, testUser);
        Inode file = manager.resolvePath("/home/a.txt", root, new com.rohith.javavirtualos.kernel.User("root", null));

        OpenFile openFile = new OpenFile(file, manager.getLifecycleManager());
        manager.getLifecycleManager().incrementOpenReference(file);
        int fd = fdTable.allocate(openFile);

        fdTable.close(fd);

        assertEquals(0, file.getOpenReferenceCount());
    }

    @Test
    public void testFinalCloseReclaimsInode() throws FileSystemException {
        manager.createFile("/home/a.txt", root, testUser);
        Inode file = manager.resolvePath("/home/a.txt", root, new com.rohith.javavirtualos.kernel.User("root", null));

        OpenFile openFile = new OpenFile(file, manager.getLifecycleManager());
        manager.getLifecycleManager().incrementOpenReference(file);
        int fd = fdTable.allocate(openFile);

        manager.remove("/home/a.txt", root, "/", false, testUser);
        
        // Link count is 0, but still open
        assertFalse(file.canBeReclaimed());

        fdTable.close(fd);
        
        // Now fully reclaimed
        assertTrue(file.canBeReclaimed());
    }

    @Test
    public void testDataRemainsAccessibleAfterUnlink() throws Exception {
        manager.createFile("/tmp", root, testUser); // create dir /tmp Wait, need createDirectory!
        manager.remove("/tmp", root, "/", false, testUser); // Clean it up since root has /tmp maybe? No, root starts empty except what we add. Let's use /home.
        
        manager.createFile("/home/data.txt", root, testUser);
        FileNode file = (FileNode) manager.resolvePath("/home/data.txt", root, new com.rohith.javavirtualos.kernel.User("root", null));
        manager.getFileStorage().write(file, 0, "hello".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        file.getMetadata().setSize(5);

        OpenFile openFile = new OpenFile(file, manager.getLifecycleManager());
        manager.getLifecycleManager().incrementOpenReference(file);
        int fd = fdTable.allocate(openFile);

        // Unlink the file
        manager.remove("/home/data.txt", root, "/", false, testUser);

        // Read from FD
        OpenFile retrieved = (OpenFile) fdTable.get(fd).orElseThrow();
        FileNode retrievedFile = (FileNode) retrieved.getFile();
        
        byte[] data = manager.getFileStorage().read(retrievedFile, 0, 5);
        assertEquals("hello", new String(data, java.nio.charset.StandardCharsets.UTF_8));

        // Close FD
        fdTable.close(fd);
        assertTrue(file.canBeReclaimed());
    }
}
