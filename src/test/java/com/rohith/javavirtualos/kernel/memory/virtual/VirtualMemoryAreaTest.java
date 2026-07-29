package com.rohith.javavirtualos.kernel.memory.virtual;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VirtualMemoryAreaTest {

    @Test
    void testVmaContainsAddress() {
        VirtualAddress start = new VirtualAddress(4096);
        VirtualAddress end = new VirtualAddress(8192);
        VirtualMemoryArea vma = new VirtualMemoryArea(start, end, "rwx", VMAType.HEAP, null, 0, PageSize.STANDARD);

        assertTrue(vma.contains(new VirtualAddress(4096)));
        assertTrue(vma.contains(new VirtualAddress(5000)));
        assertFalse(vma.contains(new VirtualAddress(8192))); // end is exclusive
        assertFalse(vma.contains(new VirtualAddress(1000)));
        
        assertEquals(PageSize.STANDARD, vma.getPageSize());
        assertEquals(VMAType.HEAP, vma.getType());
    }
}
