package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.kernel.SecurityManager;
import com.rohith.javavirtualos.kernel.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PathResolverTest {

    private DirectoryNode root;
    private DirectoryNode home;
    private DirectoryNode user;
    private PathResolver resolver;
    private User rootUser;
    private SecurityManager securityManager;

    @BeforeEach
    public void setUp() {
        root = new DirectoryNode("root");
        home = new DirectoryNode("root");
        user = new DirectoryNode("user");
        
        root.addChild("home", home);
        home.addChild("javavm", user);
        
        resolver = new PathResolver(root);
        rootUser = new User("root", null);
        securityManager = new SecurityManager();
    }

    @Test
    public void testResolveRoot() {
        Inode node = resolver.resolvePath("/", user, rootUser, securityManager);
        assertEquals(root, node);
    }

    @Test
    public void testResolveParent() {
        Inode node = resolver.resolvePath("/home/javavm/..", root, rootUser, securityManager);
        assertEquals(home, node);
        
        Inode rootNode = resolver.resolvePath("/home/javavm/../..", root, rootUser, securityManager);
        assertEquals(root, rootNode);
    }

    @Test
    public void testResolveCurrent() {
        Inode node = resolver.resolvePath(".", user, rootUser, securityManager);
        assertEquals(user, node);
    }

    @Test
    public void testResolveAbsolute() {
        Inode node = resolver.resolvePath("/home/javavm", root, rootUser, securityManager);
        assertEquals(user, node);
    }
    
    @Test
    public void testResolveHomeShortcut() {
        Inode node = resolver.resolvePath("~", root, rootUser, securityManager);
        assertEquals(user, node); // Assuming default home logic resolves to /home/javavm
    }

    @Test
    public void testResolveInvalidPath() {
        Inode node = resolver.resolvePath("/home/fakeuser", root, rootUser, securityManager);
        assertNull(node);
    }
}
