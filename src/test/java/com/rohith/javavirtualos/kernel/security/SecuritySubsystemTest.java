package com.rohith.javavirtualos.kernel.security;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.SecurityEvent;
import com.rohith.javavirtualos.kernel.vfs.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecuritySubsystemTest {

    @Test
    void testAuthenticationAndAuthorization() {
        KernelEventBus bus = new KernelEventBus();
        SecurityManager sec = new SecurityManager(bus);
        SessionManager session = new SessionManager(sec, bus);
        
        // Setup Users
        User alice = sec.createUser("alice", "pass123");
        User bob = sec.createUser("bob", "pass456");
        User root = sec.getUser(0).get();
        
        // Authentication
        assertTrue(session.login("alice", "pass123"));
        assertFalse(session.login("alice", "wrongpass"));
        
        // VFS Setup
        FileSystemStatistics stats = new FileSystemStatistics();
        VirtualFileSystem vfs = new VirtualFileSystem(bus, stats, sec);
        VfsDirectory rootDir = vfs.getRootMount().getRoot();
        
        // Root creates a home dir for alice, with 700 (owner only)
        VfsDirectory aliceHome = vfs.createDirectory(root, Path.of("alice_home"), rootDir, PermissionBits.fromOctal("700"));
        
        // Bob shouldn't be able to create a file in alice's home (parent dir is 700 owned by root)
        // Wait, root created it, so it's owned by root.
        // Let's chown it to alice (simulated manually)
        aliceHome.getINode().setOwnerUid(alice.getUid());
        
        // Now alice creates a file in her home dir
        VfsFile aliceFile = vfs.createFile(alice, Path.of("secret.txt"), aliceHome, PermissionBits.fromOctal("600"));
        assertNotNull(aliceFile);
        
        // Bob attempts to delete alice's file
        assertThrows(SecurityException.class, () -> {
            vfs.delete(bob, Path.of("secret.txt"), aliceHome);
        });
        
        // Alice attempts to delete alice's file (should work)
        assertTrue(vfs.delete(alice, Path.of("secret.txt"), aliceHome));
        
        // Root deletes alice's home (works despite 700 because root bypasses)
        assertTrue(vfs.delete(root, Path.of("alice_home"), rootDir));
    }
}
