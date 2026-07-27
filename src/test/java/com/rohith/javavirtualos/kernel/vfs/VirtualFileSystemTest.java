package com.rohith.javavirtualos.kernel.vfs;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import org.junit.jupiter.api.Test;

import com.rohith.javavirtualos.kernel.security.SecurityManager;
import com.rohith.javavirtualos.kernel.security.User;
import com.rohith.javavirtualos.kernel.security.Role;
import com.rohith.javavirtualos.kernel.security.PermissionBits;

import static org.junit.jupiter.api.Assertions.*;

class VirtualFileSystemTest {

    @Test
    void testVfsBasicOperations() {
        KernelEventBus bus = new KernelEventBus();
        FileSystemStatistics stats = new FileSystemStatistics();
        SecurityManager securityManager = new SecurityManager(bus);
        VirtualFileSystem vfs = new VirtualFileSystem(bus, stats, securityManager);
        User rootUser = new User(0, "root", "root", Role.ROOT, 0);
        
        VfsDirectory root = vfs.getRootMount().getRoot();
        
        // 1. Mkdir
        VfsDirectory home = vfs.createDirectory(rootUser, Path.of("home"), root, PermissionBits.fromOctal("0755"));
        assertNotNull(home);
        assertTrue(root.getChild("home").isPresent());
        
        // 2. Touch
        VfsFile file = vfs.createFile(rootUser, Path.of("home/test.txt"), root, PermissionBits.fromOctal("0644"));
        assertNotNull(file);
        assertTrue(home.getChild("test.txt").isPresent());
        
        // 3. Resolve Absolute
        assertTrue(vfs.getResolver().resolve(Path.of("/home/test.txt"), root).isPresent());
        
        // 4. Resolve Relative (../home/test.txt from root)
        assertTrue(vfs.getResolver().resolve(Path.of("../home/test.txt"), root).isPresent());
        
        // 5. Delete file
        assertTrue(vfs.delete(rootUser, Path.of("/home/test.txt"), root));
        assertFalse(home.getChild("test.txt").isPresent());
        
        // 6. Delete directory
        assertTrue(vfs.delete(rootUser, Path.of("/home"), root));
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
        SecurityManager securityManager = new SecurityManager(bus);
        VirtualFileSystem vfs = new VirtualFileSystem(bus, stats, securityManager);
        User rootUser = new User(0, "root", "root", Role.ROOT, 0);
        
        VfsDirectory root = vfs.getRootMount().getRoot();
        vfs.createDirectory(rootUser, Path.of("a"), root, PermissionBits.fromOctal("0755"));
        VfsDirectory a = (VfsDirectory) vfs.getResolver().resolve(Path.of("a"), root).get();
        vfs.createDirectory(rootUser, Path.of("b"), a, PermissionBits.fromOctal("0755"));
        
        VfsValidator validator = new VfsValidator();
        assertDoesNotThrow(() -> validator.validate(vfs));
    }
}
