package com.rohith.javavirtualos.filesystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileNodeTest {

    private DirectoryNode root;
    private FileNode file;

    @BeforeEach
    public void setUp() {
        root = new DirectoryNode("root");
        file = new FileNode("user");
        root.addChild("test.txt", file);
    }

    @Test
    public void testMetadataSize() {
        assertEquals(0, file.calculateSize());
        
        file.getMetadata().setSize(8);
        assertEquals(8, file.calculateSize());
        assertEquals(8, file.getMetadata().getSize());
        
        file.getMetadata().setSize(9);
        assertEquals(9, file.calculateSize());
    }

    @Test
    public void testType() {
        assertEquals(FileType.FILE, file.getType());
    }
}
