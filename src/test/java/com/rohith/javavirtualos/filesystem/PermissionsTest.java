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

public class PermissionsTest {

    private FileSystemManager fsManager;
    private User root;
    private User alice;
    private User bob;

    @BeforeEach
    public void setup() throws Exception {
        fsManager = new FileSystemManager();
        SecurityManager securityManager = new SecurityManager();
        fsManager.setSecurityManager(securityManager);
        
        root = new User("root", null);
        
        alice = new User("alice", null);
        alice.addGroup("developers");
        
        bob = new User("bob", null);
        
        // Setup /home and users
        fsManager.createDirectory("/home", fsManager.getRoot(), root);
        fsManager.createDirectory("/home/alice", fsManager.resolveDirectory("/home", fsManager.getRoot(), root), root);
        fsManager.chown("/home/alice", fsManager.getRoot(), "alice", root);
        fsManager.createDirectory("/home/bob", fsManager.resolveDirectory("/home", fsManager.getRoot(), root), root);
        fsManager.chown("/home/bob", fsManager.getRoot(), "bob", root);
    }

    @Test
    public void ownerReadWriteExecute() throws Exception {
        fsManager.createFile("/home/alice/test.txt", fsManager.getRoot(), alice);
        fsManager.chmod("/home/alice/test.txt", fsManager.getRoot(), (short) 0700, alice);
        
        Inode file = fsManager.resolvePath("/home/alice/test.txt", fsManager.getRoot(), alice);
        assertNotNull(file);
        
        assertDoesNotThrow(() -> fsManager.validateReadAccess(file, alice));
        assertDoesNotThrow(() -> fsManager.validateWriteAccess(file, alice));
        assertDoesNotThrow(() -> fsManager.validateExecuteAccess(file, alice));
    }

    @Test
    public void groupPermissions() throws Exception {
        fsManager.createFile("/home/alice/shared.txt", fsManager.getRoot(), alice);
        fsManager.chmod("/home/alice/shared.txt", fsManager.getRoot(), (short) 0060, alice);
        fsManager.chgrp("/home/alice/shared.txt", fsManager.getRoot(), "developers", alice);
        
        Inode file = fsManager.resolvePath("/home/alice/shared.txt", fsManager.getRoot(), root);
        
        User charlie = new User("charlie", null);
        charlie.addGroup("developers");
        
        assertDoesNotThrow(() -> fsManager.validateReadAccess(file, charlie));
        assertDoesNotThrow(() -> fsManager.validateWriteAccess(file, charlie));
        assertThrows(FileSystemException.class, () -> fsManager.validateExecuteAccess(file, charlie));
    }

    @Test
    public void otherPermissions() throws Exception {
        fsManager.createFile("/home/alice/public.txt", fsManager.getRoot(), alice);
        fsManager.chgrp("/home/alice/public.txt", fsManager.getRoot(), "developers", alice); // bob is not in developers
        fsManager.chmod("/home/alice/public.txt", fsManager.getRoot(), (short) 0004, alice);
        
        Inode file = fsManager.resolvePath("/home/alice/public.txt", fsManager.getRoot(), root);
        
        assertDoesNotThrow(() -> fsManager.validateReadAccess(file, bob));
        assertThrows(FileSystemException.class, () -> fsManager.validateWriteAccess(file, bob));
    }

    @Test
    public void ownerPrecedenceOverGroup() throws Exception {
        fsManager.createFile("/home/alice/weird.txt", fsManager.getRoot(), alice);
        // Owner has no read, group has read
        fsManager.chmod("/home/alice/weird.txt", fsManager.getRoot(), (short) 0040, alice);
        fsManager.chgrp("/home/alice/weird.txt", fsManager.getRoot(), "developers", alice);
        
        Inode file = fsManager.resolvePath("/home/alice/weird.txt", fsManager.getRoot(), root);
        
        // Alice is owner, but owner bits are 0. Group bits are 4, Alice is in group, but owner takes precedence!
        assertThrows(FileSystemException.class, () -> fsManager.validateReadAccess(file, alice));
    }

    @Test
    public void rootOverride() throws Exception {
        fsManager.createFile("/home/alice/secret.txt", fsManager.getRoot(), alice);
        fsManager.chmod("/home/alice/secret.txt", fsManager.getRoot(), (short) 0000, alice);
        
        Inode file = fsManager.resolvePath("/home/alice/secret.txt", fsManager.getRoot(), root);
        
        assertDoesNotThrow(() -> fsManager.validateReadAccess(file, root));
        assertDoesNotThrow(() -> fsManager.validateWriteAccess(file, root));
    }

    @Test
    public void mode0600() throws Exception {
        fsManager.createFile("/home/alice/0600.txt", fsManager.getRoot(), alice);
        fsManager.chmod("/home/alice/0600.txt", fsManager.getRoot(), (short) 0600, alice);
        
        Inode file = fsManager.resolvePath("/home/alice/0600.txt", fsManager.getRoot(), root);
        assertThrows(FileSystemException.class, () -> fsManager.validateReadAccess(file, bob));
    }

    @Test
    public void mode0660() throws Exception {
        fsManager.createFile("/home/alice/0660.txt", fsManager.getRoot(), alice);
        fsManager.chmod("/home/alice/0660.txt", fsManager.getRoot(), (short) 0660, alice);
        fsManager.chgrp("/home/alice/0660.txt", fsManager.getRoot(), "developers", alice);
        
        Inode file = fsManager.resolvePath("/home/alice/0660.txt", fsManager.getRoot(), root);
        
        User charlie = new User("charlie", null);
        charlie.addGroup("developers");
        
        assertDoesNotThrow(() -> fsManager.validateReadAccess(file, charlie));
        assertThrows(FileSystemException.class, () -> fsManager.validateReadAccess(file, bob)); // bob not in developers
    }

    @Test
    public void mode0000() throws Exception {
        fsManager.createFile("/home/alice/0000.txt", fsManager.getRoot(), alice);
        fsManager.chmod("/home/alice/0000.txt", fsManager.getRoot(), (short) 0000, alice);
        
        Inode file = fsManager.resolvePath("/home/alice/0000.txt", fsManager.getRoot(), root);
        
        assertThrows(FileSystemException.class, () -> fsManager.validateReadAccess(file, alice));
        assertThrows(FileSystemException.class, () -> fsManager.validateReadAccess(file, bob));
    }

    @Test
    public void directoryExecuteTraversal() throws Exception {
        fsManager.createDirectory("/home/alice/secure", fsManager.getRoot(), alice);
        fsManager.createFile("/home/alice/secure/data.txt", fsManager.getRoot(), alice);
        
        // Remove execute from others
        fsManager.chmod("/home/alice/secure", fsManager.getRoot(), (short) 0700, alice);
        
        // Bob cannot traverse
        assertThrows(FileSystemException.class, () -> fsManager.resolvePath("/home/alice/secure/data.txt", fsManager.getRoot(), bob));
    }

    @Test
    public void directoryReadVsExecute() throws Exception {
        fsManager.createDirectory("/home/alice/dir1", fsManager.getRoot(), alice);
        fsManager.createFile("/home/alice/dir1/data.txt", fsManager.getRoot(), alice);
        
        // Execute only
        fsManager.chmod("/home/alice/dir1", fsManager.getRoot(), (short) 0111, alice);
        
        // Traversal works
        Inode node = fsManager.resolvePath("/home/alice/dir1/data.txt", fsManager.getRoot(), bob);
        assertNotNull(node);
        
        // Reading dir fails
        DirectoryNode dir = fsManager.resolveDirectory("/home/alice/dir1", fsManager.getRoot(), root);
        assertThrows(FileSystemException.class, () -> fsManager.validateReadAccess(dir, bob));
    }

    @Test
    public void symlinkTraversalPermission() throws Exception {
        fsManager.createDirectory("/home/alice/secure", fsManager.getRoot(), alice);
        fsManager.createFile("/home/alice/secure/data.txt", fsManager.getRoot(), alice);
        fsManager.chmod("/home/alice/secure", fsManager.getRoot(), (short) 0700, alice); // locked
        
        fsManager.createSymlink("/home/alice/secure/data.txt", "/home/bob/link", fsManager.getRoot(), root);
        
        // Bob follows link but it resolves to a secure directory he can't traverse
        assertThrows(FileSystemException.class, () -> fsManager.resolvePath("/home/bob/link", fsManager.getRoot(), bob));
    }

    @Test
    public void chmodOwner() throws Exception {
        fsManager.createFile("/home/alice/test.txt", fsManager.getRoot(), alice);
        assertDoesNotThrow(() -> fsManager.chmod("/home/alice/test.txt", fsManager.getRoot(), (short) 0777, alice));
    }

    @Test
    public void chmodRoot() throws Exception {
        fsManager.createFile("/home/alice/test.txt", fsManager.getRoot(), alice);
        assertDoesNotThrow(() -> fsManager.chmod("/home/alice/test.txt", fsManager.getRoot(), (short) 0777, root));
    }

    @Test
    public void chmodUnauthorized() throws Exception {
        fsManager.createFile("/home/alice/test.txt", fsManager.getRoot(), alice);
        assertThrows(FileSystemException.class, () -> fsManager.chmod("/home/alice/test.txt", fsManager.getRoot(), (short) 0777, bob));
    }

    @Test
    public void chownRoot() throws Exception {
        fsManager.createFile("/home/alice/test.txt", fsManager.getRoot(), alice);
        assertDoesNotThrow(() -> fsManager.chown("/home/alice/test.txt", fsManager.getRoot(), "bob", root));
    }

    @Test
    public void chownUnauthorized() throws Exception {
        fsManager.createFile("/home/alice/test.txt", fsManager.getRoot(), alice);
        assertThrows(FileSystemException.class, () -> fsManager.chown("/home/alice/test.txt", fsManager.getRoot(), "bob", alice)); // owner can't chown
    }

    @Test
    public void chgrpMember() throws Exception {
        fsManager.createFile("/home/alice/test.txt", fsManager.getRoot(), alice);
        // Alice is in developers
        assertDoesNotThrow(() -> fsManager.chgrp("/home/alice/test.txt", fsManager.getRoot(), "developers", alice));
    }

    @Test
    public void chgrpNonMember() throws Exception {
        fsManager.createFile("/home/alice/test.txt", fsManager.getRoot(), alice);
        // Alice is NOT in admins
        assertThrows(FileSystemException.class, () -> fsManager.chgrp("/home/alice/test.txt", fsManager.getRoot(), "admins", alice));
    }

    @Test
    public void umaskCreation() throws Exception {
        alice.setUmask((short) 0022);
        fsManager.createFile("/home/alice/umask.txt", fsManager.getRoot(), alice);
        
        Inode file = fsManager.resolvePath("/home/alice/umask.txt", fsManager.getRoot(), root);
        assertEquals((short) 0644, file.getMetadata().getMode().getMode());
        
        fsManager.createDirectory("/home/alice/umaskdir", fsManager.getRoot(), alice);
        Inode dir = fsManager.resolveDirectory("/home/alice/umaskdir", fsManager.getRoot(), root);
        assertEquals((short) 0755, dir.getMetadata().getMode().getMode());
    }
}
