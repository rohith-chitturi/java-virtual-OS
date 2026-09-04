package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PathResolverTest {

    private DirectoryNode root;
    private DirectoryNode home;
    private DirectoryNode user;
    private PathResolver resolver;

    @BeforeEach
    public void setUp() {
        root = new DirectoryNode("root");
        home = new DirectoryNode("root");
        user = new DirectoryNode("user");
        
        root.addChild("home", home);
        home.addChild("javavm", user);
        
        resolver = new PathResolver(root);
    }

    @Test
    public void testResolveRoot() {
        Inode node = resolver.resolvePath("/", user);
        assertEquals(root, node);
    }

    @Test
    public void testResolveParent() {
        Inode node = resolver.resolvePath("/home/javavm/..", root);
        assertEquals(home, node);
        
        Inode rootNode = resolver.resolvePath("/home/javavm/../..", root);
        assertEquals(root, rootNode);
    }

    @Test
    public void testResolveCurrent() {
        Inode node = resolver.resolvePath(".", user);
        assertEquals(user, node);
    }

    @Test
    public void testResolveAbsolute() {
        Inode node = resolver.resolvePath("/home/javavm", root);
        assertEquals(user, node);
    }
    
    @Test
    public void testResolveHomeShortcut() {
        Inode node = resolver.resolvePath("~", root);
        assertEquals(user, node);
    }

    @Test
    public void testResolveInvalidPath() {
        Inode node = resolver.resolvePath("/home/fakeuser", root);
        assertNull(node);
    }
}
