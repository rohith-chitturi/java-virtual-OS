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
        root = new DirectoryNode("", "root", null);
        home = new DirectoryNode("home", "root", root);
        user = new DirectoryNode("javavm", "user", home);
        
        root.addChild(home);
        home.addChild(user);
        
        resolver = new PathResolver(root);
    }

    @Test
    public void testResolveRoot() {
        Inode node = resolver.resolvePath("/", user);
        assertEquals(root, node);
    }

    @Test
    public void testResolveParent() {
        Inode node = resolver.resolvePath("..", user);
        assertEquals(home, node);
        
        Inode rootNode = resolver.resolvePath("../..", user);
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
