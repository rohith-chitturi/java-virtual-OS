package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.kernel.SecurityManager;
import com.rohith.javavirtualos.kernel.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileSystemManagerTest {

    private FileSystemManager manager;
    private User testUser;
    private User rootUser;

    @BeforeEach
    public void setUp() {
        manager = new FileSystemManager();
        manager.setSecurityManager(new SecurityManager());
        testUser = new User("user", "pass");
        rootUser = new User("root", null);
    }

    @Test
    public void testCreateDirectory() {
        manager.createDirectory("documents", manager.getRoot(), rootUser);
        Inode node = manager.resolvePath("/documents", manager.getRoot(), rootUser);
        assertNotNull(node);
    }

    @Test
    public void testCreateFile() {
        manager.createFile("notes.txt", manager.getRoot(), rootUser);
        assertTrue(manager.resolvePath("/notes.txt", manager.getRoot(), rootUser) instanceof FileNode);
    }

    @Test
    public void testRemoveFile() {
        manager.createFile("notes.txt", manager.getRoot(), rootUser);
        manager.remove("notes.txt", manager.getRoot(), "/", false, rootUser);
        assertNull(manager.resolvePath("/notes.txt", manager.getRoot(), rootUser));
    }

    @Test
    public void testRemoveDirectory() {
        manager.createDirectory("docs", manager.getRoot(), rootUser);
        manager.remove("docs", manager.getRoot(), "/", true, rootUser);
        assertNull(manager.resolvePath("/docs", manager.getRoot(), rootUser));
    }

    @Test
    public void testRemoveNonEmptyDirectoryFails() {
        manager.createDirectory("docs", manager.getRoot(), rootUser);
        DirectoryNode docs = (DirectoryNode) manager.resolvePath("docs", manager.getRoot(), rootUser);
        manager.createFile("file.txt", docs, rootUser);
        
        assertThrows(FileSystemException.class, () -> manager.remove("docs", manager.getRoot(), "/", true, rootUser));
    }
}
