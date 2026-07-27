package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.kernel.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileSystemManagerTest {

    private FileSystemManager manager;
    private User testUser;

    @BeforeEach
    public void setUp() {
        manager = new FileSystemManager();
        testUser = new User("user", "pass");
    }

    @Test
    public void testCreateDirectory() {
        manager.createDirectory("documents", manager.getRoot(), testUser);
        assertNotNull(manager.resolvePath("/documents", manager.getRoot()));
    }

    @Test
    public void testCreateFile() {
        manager.createFile("notes.txt", manager.getRoot(), testUser);
        assertTrue(manager.resolvePath("/notes.txt", manager.getRoot()) instanceof FileNode);
    }

    @Test
    public void testRemoveFile() {
        manager.createFile("notes.txt", manager.getRoot(), testUser);
        manager.remove("notes.txt", manager.getRoot(), false, testUser);
        assertNull(manager.resolvePath("/notes.txt", manager.getRoot()));
    }

    @Test
    public void testRemoveDirectory() {
        manager.createDirectory("docs", manager.getRoot(), testUser);
        manager.remove("docs", manager.getRoot(), true, testUser);
        assertNull(manager.resolvePath("/docs", manager.getRoot()));
    }

    @Test
    public void testRemoveNonEmptyDirectoryFails() {
        manager.createDirectory("docs", manager.getRoot(), testUser);
        DirectoryNode docs = (DirectoryNode) manager.resolvePath("docs", manager.getRoot());
        manager.createFile("file.txt", docs, testUser);
        
        assertThrows(FileSystemException.class, () -> manager.remove("docs", manager.getRoot(), true, testUser));
    }
}
