package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileNotFoundException;
import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileSystemManagerTest {

    private FileSystemManager manager;

    @BeforeEach
    public void setUp() {
        manager = new FileSystemManager();
    }

    @Test
    public void testCreateDirectory() {
        manager.createDirectory("documents", manager.getRoot(), "user");
        assertNotNull(manager.resolvePath("/documents", manager.getRoot()));
    }

    @Test
    public void testCreateFile() {
        manager.createFile("notes.txt", manager.getRoot(), "user");
        assertTrue(manager.resolvePath("/notes.txt", manager.getRoot()) instanceof FileNode);
    }

    @Test
    public void testRemoveFile() {
        manager.createFile("notes.txt", manager.getRoot(), "user");
        manager.remove("notes.txt", manager.getRoot(), false);
        assertNull(manager.resolvePath("/notes.txt", manager.getRoot()));
    }

    @Test
    public void testRemoveDirectory() {
        manager.createDirectory("docs", manager.getRoot(), "user");
        manager.remove("docs", manager.getRoot(), true);
        assertNull(manager.resolvePath("/docs", manager.getRoot()));
    }

    @Test
    public void testRemoveNonEmptyDirectoryFails() {
        manager.createDirectory("docs", manager.getRoot(), "user");
        DirectoryNode docs = (DirectoryNode) manager.resolvePath("docs", manager.getRoot());
        manager.createFile("file.txt", docs, "user");
        
        assertThrows(FileSystemException.class, () -> manager.remove("docs", manager.getRoot(), true));
    }
}
