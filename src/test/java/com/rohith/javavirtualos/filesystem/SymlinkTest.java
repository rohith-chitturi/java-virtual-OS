package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileNotFoundException;
import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.exceptions.TooManySymlinksException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.filesystem.model.SymlinkNode;
import com.rohith.javavirtualos.kernel.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SymlinkTest {
    private FileSystemManager manager;
    private User testUser;
    private DirectoryNode root;

    @BeforeEach
    public void setUp() throws FileSystemException {
        manager = new FileSystemManager();
        testUser = new User("testuser", "password");
        root = manager.getRoot();
        manager.createDirectory("home", root, testUser);
        manager.createDirectory("home/user", root, testUser);
    }

    @Test
    public void testCreateFileSymlink() throws FileSystemException {
        manager.createFile("/home/user/data.txt", root, testUser);
        manager.createSymlink("/home/user/data.txt", "/home/user/link", root, testUser);

        Inode symlink = manager.resolvePath("/home/user/link", root);
        assertNotNull(symlink);
        assertTrue(symlink instanceof FileNode); // because resolvePath follows the link!
    }

    @Test
    public void testReadThroughFileSymlink() throws FileSystemException {
        manager.createFile("/home/user/data.txt", root, testUser);
        FileNode dataFile = (FileNode) manager.resolvePath("/home/user/data.txt", root);
        dataFile.setContent("hello world");

        manager.createSymlink("/home/user/data.txt", "/home/user/link", root, testUser);

        FileNode resolved = (FileNode) manager.resolvePath("/home/user/link", root);
        assertEquals("hello world", resolved.getContent());
    }

    @Test
    public void testCreateDirectorySymlink() throws FileSystemException {
        manager.createDirectory("/home/user/docs", root, testUser);
        manager.createSymlink("/home/user/docs", "/home/user/docs_link", root, testUser);

        Inode resolved = manager.resolvePath("/home/user/docs_link", root);
        assertTrue(resolved instanceof DirectoryNode);
    }

    @Test
    public void testTraverseDirectorySymlink() throws FileSystemException {
        manager.createDirectory("/home/user/docs", root, testUser);
        manager.createFile("/home/user/docs/file.txt", root, testUser);
        manager.createSymlink("/home/user/docs", "/home/user/docs_link", root, testUser);

        Inode file = manager.resolvePath("/home/user/docs_link/file.txt", root);
        assertNotNull(file);
        assertTrue(file instanceof FileNode);
    }

    @Test
    public void testRelativeSymlinkResolution() throws FileSystemException {
        manager.createDirectory("/home/user/data", root, testUser);
        manager.createFile("/home/user/data/file.txt", root, testUser);
        
        manager.createSymlink("../data/file.txt", "/home/user/link", root, testUser);

        Inode resolved = manager.resolvePath("/home/user/link", root);
        assertNotNull(resolved);
        assertTrue(resolved instanceof FileNode);
    }

    @Test
    public void testAbsoluteSymlinkResolution() throws FileSystemException {
        manager.createFile("/home/user/file.txt", root, testUser);
        manager.createSymlink("/home/user/file.txt", "/home/user/link", root, testUser);

        Inode resolved = manager.resolvePath("/home/user/link", root);
        assertNotNull(resolved);
        assertTrue(resolved instanceof FileNode);
    }

    @Test
    public void testChainedSymlinkResolution() throws FileSystemException {
        manager.createFile("/home/user/file.txt", root, testUser);
        manager.createSymlink("/home/user/file.txt", "/home/user/link1", root, testUser);
        manager.createSymlink("/home/user/link1", "/home/user/link2", root, testUser);

        Inode resolved = manager.resolvePath("/home/user/link2", root);
        assertNotNull(resolved);
        assertTrue(resolved instanceof FileNode);
    }

    @Test
    public void testDanglingSymlink() throws FileSystemException {
        manager.createSymlink("/home/does/not/exist", "/home/user/link", root, testUser);
        
        Inode resolved = manager.resolvePath("/home/user/link", root);
        assertNull(resolved);
    }

    @Test
    public void testCircularSymlink() throws FileSystemException {
        manager.createSymlink("/home/user/b", "/home/user/a", root, testUser);
        manager.createSymlink("/home/user/a", "/home/user/b", root, testUser);

        assertThrows(TooManySymlinksException.class, () -> {
            manager.resolvePath("/home/user/a", root);
        });
    }

    @Test
    public void testMaxSymlinkDepth() throws FileSystemException {
        manager.createFile("/home/user/f0", root, testUser);
        for (int i = 0; i < 15; i++) {
            manager.createSymlink("/home/user/f" + i, "/home/user/f" + (i + 1), root, testUser);
        }

        assertThrows(TooManySymlinksException.class, () -> {
            manager.resolvePath("/home/user/f15", root);
        });
    }

    @Test
    public void testRmRemovesSymlinkOnly() throws FileSystemException {
        manager.createFile("/home/user/data.txt", root, testUser);
        manager.createSymlink("/home/user/data.txt", "/home/user/link", root, testUser);

        manager.remove("/home/user/link", root, "/", false, testUser);

        // Link is gone
        assertThrows(FileNotFoundException.class, () -> {
            manager.readlink("/home/user/link", root, testUser);
        });

        // Target remains
        assertNotNull(manager.resolvePath("/home/user/data.txt", root));
    }

    @Test
    public void testTargetLinkCountUnchanged() throws FileSystemException {
        manager.createFile("/home/user/data.txt", root, testUser);
        Inode data = manager.resolvePath("/home/user/data.txt", root);
        assertEquals(1, data.getLinkCount());

        manager.createSymlink("/home/user/data.txt", "/home/user/link", root, testUser);
        assertEquals(1, data.getLinkCount());
    }

    @Test
    public void testSymlinkCreationDoesNotRequireTarget() throws FileSystemException {
        // Asserting that it simply doesn't throw
        manager.createSymlink("/does/not/exist", "/home/user/dangling", root, testUser);
        assertEquals("/does/not/exist", manager.readlink("/home/user/dangling", root, testUser));
    }
}
