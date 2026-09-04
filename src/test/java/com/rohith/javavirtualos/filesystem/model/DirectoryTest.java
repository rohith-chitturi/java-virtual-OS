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
        root = new DirectoryNode("root");
        usr = new DirectoryNode("root");
        textFile = new FileNode("root");
    }

    @Test
    public void testAddAndRemoveChild() {
        root.addChild("usr", usr);
        root.addChild("hello.txt", textFile);
        
        assertTrue(root.hasChild("usr"));
        assertTrue(root.hasChild("hello.txt"));
        
        root.removeChild("usr");
        assertFalse(root.hasChild("usr"));
    }

    @Test
    public void testCalculateSize() {
        root.addChild("usr", usr);
        root.addChild("hello.txt", textFile);
        
        textFile.setContent("hello world");
        
        assertEquals(11, root.calculateSize());
    }
}
