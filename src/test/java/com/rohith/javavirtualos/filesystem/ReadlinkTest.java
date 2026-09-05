package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.kernel.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReadlinkTest {
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
    public void testReturnsLiteralTarget() throws FileSystemException {
        manager.createSymlink("/home/target", "/home/link", root, testUser);
        assertEquals("/home/target", manager.readlink("/home/link", root, testUser));
    }

    @Test
    public void testWorksForRelativeTarget() throws FileSystemException {
        manager.createSymlink("../target", "/home/link", root, testUser);
        assertEquals("../target", manager.readlink("/home/link", root, testUser));
    }

    @Test
    public void testFailsForNonSymlink() throws FileSystemException {
        manager.createFile("/home/file.txt", root, testUser);
        assertThrows(FileSystemException.class, () -> {
            manager.readlink("/home/file.txt", root, testUser);
        });
    }
}
