package com.rohith.javavirtualos.filesystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DirectoryTest {

    private DirectoryNode root;
    private DirectoryNode usr;
    private FileNode textFile;

    @BeforeEach
    public void setUp() {
        root = new DirectoryNode("", "root", null);
        usr = new DirectoryNode("usr", "root", root);
        textFile = new FileNode("hello.txt", "root", root);
    }

    @Test
    public void testAddAndRemoveChild() {
        root.addChild(usr);
        root.addChild(textFile);
        
        assertTrue(root.hasChild("usr"));
        assertTrue(root.hasChild("hello.txt"));
        
        root.removeChild("usr");
        assertFalse(root.hasChild("usr"));
    }

    @Test
    public void testCalculateSize() {
        root.addChild(usr);
        root.addChild(textFile);
        
        textFile.setContent("hello world");
        
        assertEquals(11, root.calculateSize());
    }

    @Test
    public void testAbsolutePath() {
        root.addChild(usr);
        DirectoryNode bin = new DirectoryNode("bin", "root", usr);
        usr.addChild(bin);
        
        assertEquals("/", root.getAbsolutePath());
        assertEquals("/usr", usr.getAbsolutePath());
        assertEquals("/usr/bin", bin.getAbsolutePath());
    }
}
