package com.rohith.javavirtualos.kernel.events;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KernelLogBufferTest {

    @Test
    public void testRingBufferCapacity() {
        KernelLogBuffer buffer = new KernelLogBuffer(3, null);
        
        buffer.log("A");
        buffer.log("B");
        buffer.log("C");
        buffer.log("D"); // Should push out A
        
        String contents = buffer.getContents();
        
        assertFalse(contents.contains("A"));
        assertTrue(contents.contains("B"));
        assertTrue(contents.contains("C"));
        assertTrue(contents.contains("D"));
        
        // Ensure exact formatting with message counts
        String[] lines = contents.trim().split("\n");
        assertEquals(3, lines.length);
        assertEquals("[2] B", lines[0]);
        assertEquals("[3] C", lines[1]);
        assertEquals("[4] D", lines[2]);
    }
}
