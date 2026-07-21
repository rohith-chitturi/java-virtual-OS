package com.rohith.javavirtualos.kernel.vfs;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class VirtualFileSystemTest {

    @Test
    void testVfsBasicOperations() {
        KernelEventBus bus = new KernelEventBus();
        FileSystemStatistics stats = new FileSystemStatistics();
        VirtualFileSystem vfs = new VirtualFileSystem(bus, stats);
        
        VfsDirectory root = vfs.getRootMount().getRoot();
        
        // 1. Mkdir
        VfsDirectory home = vfs.createDirectory(Path.of("home"), root, 0, EnumSet.allOf(Permission.class));
        assertNotNull(home);
        assertTrue(root.getChild("home").isPresent());
        
        // 2. Touch
        VfsFile file = vfs.createFile(Path.of("home/test.txt"), root, 0, EnumSet.of(Permission.READ, Permission.WRITE));
        assertNotNull(file);
        assertTrue(home.getChild("test.txt").isPresent());
        
        // 3. Resolve Absolute
        assertTrue(vfs.getResolver().resolve(Path.of("/home/test.txt"), root).isPresent());
        
        // 4. Resolve Relative (../home/test.txt from root)
        assertTrue(vfs.getResolver().resolve(Path.of("../home/test.txt"), root).isPresent());
        
        // 5. Delete file
        assertTrue(vfs.delete(Path.of("/home/test.txt"), root));
        assertFalse(home.getChild("test.txt").isPresent());
        
        // 6. Delete directory
        assertTrue(vfs.delete(Path.of("/home"), root));
        assertFalse(root.getChild("home").isPresent());
        
        // Stats
        assertEquals(1, stats.getTotalFiles());
        assertEquals(2, stats.getTotalDirectories()); // / and /home
        assertEquals(2, stats.getDeletes());
    }

    @Test
    void testVfsValidation() {
        KernelEventBus bus = new KernelEventBus();
        FileSystemStatistics stats = new FileSystemStatistics();
        VirtualFileSystem vfs = new VirtualFileSystem(bus, stats);
        
        VfsDirectory root = vfs.getRootMount().getRoot();
        vfs.createDirectory(Path.of("a"), root, 0, EnumSet.allOf(Permission.class));
        VfsDirectory a = (VfsDirectory) vfs.getResolver().resolve(Path.of("a"), root).get();
        vfs.createDirectory(Path.of("b"), a, 0, EnumSet.allOf(Permission.class));
        
        VfsValidator validator = new VfsValidator();
        assertDoesNotThrow(() -> validator.validate(vfs));
    }
}
