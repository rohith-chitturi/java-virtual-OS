package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.kernel.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HardLinkTest {
    private FileSystemManager manager;
    private User testUser;
    private DirectoryNode root;

    @BeforeEach
    public void setUp() throws FileSystemException {
        manager = new FileSystemManager();
        testUser = new User("testuser", "password");
        root = manager.getRoot();
        manager.createDirectory("home", root, testUser);
    }

    @Test
    public void testCreateHardLink() throws FileSystemException {
        manager.createFile("/home/a.txt", root, testUser);
        Inode fileA = manager.resolvePath("/home/a.txt", root);

        manager.createHardLink("/home/a.txt", "/home/b.txt", root, testUser);
        Inode fileB = manager.resolvePath("/home/b.txt", root);

        assertEquals(fileA, fileB);
        assertEquals(fileA.getInodeId(), fileB.getInodeId());
    }

    @Test
    public void testLinkCountIncrements() throws FileSystemException {
        manager.createFile("/home/a.txt", root, testUser);
        Inode fileA = manager.resolvePath("/home/a.txt", root);
        assertEquals(1, fileA.getLinkCount());

        manager.createHardLink("/home/a.txt", "/home/b.txt", root, testUser);
        assertEquals(2, fileA.getLinkCount());
    }

    @Test
    public void testDeleteOneLinkPreservesInode() throws FileSystemException {
        manager.createFile("/home/a.txt", root, testUser);
        manager.createHardLink("/home/a.txt", "/home/b.txt", root, testUser);
        Inode file = manager.resolvePath("/home/a.txt", root);

        manager.remove("/home/a.txt", root, "/", false, testUser);
        
        assertEquals(1, file.getLinkCount());
        assertNull(manager.resolvePath("/home/a.txt", root));
        assertNotNull(manager.resolvePath("/home/b.txt", root));
    }

    @Test
    public void testDeleteFinalLinkRemovesInode() throws FileSystemException {
        manager.createFile("/home/a.txt", root, testUser);
        manager.createHardLink("/home/a.txt", "/home/b.txt", root, testUser);
        Inode file = manager.resolvePath("/home/a.txt", root);

        manager.remove("/home/a.txt", root, "/", false, testUser);
        manager.remove("/home/b.txt", root, "/", false, testUser);
        
        assertEquals(0, file.getLinkCount());
        assertTrue(file.canBeReclaimed());
    }

    @Test
    public void testRejectDirectoryHardLink() {
        manager.createDirectory("/home/dir1", root, testUser);
        assertThrows(FileSystemException.class, () -> {
            manager.createHardLink("/home/dir1", "/home/dir2", root, testUser);
        });
    }
}
