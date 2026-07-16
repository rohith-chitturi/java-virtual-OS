package com.rohith.javavirtualos.filesystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileNodeTest {

    private DirectoryNode root;
    private FileNode file;

    @BeforeEach
    public void setUp() {
        root = new DirectoryNode("", "root", null);
        file = new FileNode("test.txt", "user", root);
        root.addChild(file);
    }

    @Test
    public void testContentModification() {
        assertEquals("", file.getContent());
        assertEquals(0, file.calculateSize());
        
        file.setContent("Hello OS");
        assertEquals("Hello OS", file.getContent());
        assertEquals(8, file.calculateSize());
        assertEquals(8, file.getMetadata().getSize());
        
        file.appendContent("!");
        assertEquals("Hello OS!", file.getContent());
        assertEquals(9, file.calculateSize());
    }

    @Test
    public void testType() {
        assertEquals(FileType.FILE, file.getType());
    }
}
