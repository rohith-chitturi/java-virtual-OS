package com.rohith.javavirtualos.filesystem.model;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

public class VirtualFileNodeTest {

    @Test
    public void testDynamicContentEvaluation() {
        AtomicInteger counter = new AtomicInteger(0);
        VirtualFileNode node = new VirtualFileNode("root", () -> "Count: " + counter.incrementAndGet());
        
        assertEquals("Count: 1", node.getContent());
        assertEquals("Count: 2", node.getContent());
        assertEquals("Count: 3", node.getContent());
    }

    @Test
    public void testCalculateSizeIsDynamic() {
        AtomicInteger counter = new AtomicInteger(10);
        // Returns strings of length 1, 2, 3...
        VirtualFileNode node = new VirtualFileNode("root", () -> "A".repeat(counter.incrementAndGet() % 5 + 1));
        
        long size1 = node.calculateSize();
        long size2 = node.calculateSize();
        assertNotEquals(size1, size2);
    }

    @Test
    public void testReadOnly() {
        VirtualFileNode node = new VirtualFileNode("root", () -> "static");
        assertThrows(UnsupportedOperationException.class, () -> node.setContent("new"));
        assertThrows(UnsupportedOperationException.class, () -> node.appendContent("more"));
    }
}
