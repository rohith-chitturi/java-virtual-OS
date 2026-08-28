package com.rohith.javavirtualos.kernel.memory.virtual;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HugePageTest {

    @Test
    void testHugePageMath() {
        VirtualAddress vAddr = new VirtualAddress(3 * 1024 * 1024); // 3MB mark
        
        long standardPageSize = PageSize.STANDARD.getBytes();
        assertEquals(768, vAddr.getPageNumber(standardPageSize)); // 3 * 1024 * 1024 / 4096 = 768
        
        long hugePageSize = PageSize.HUGE.getBytes();
        assertEquals(1, vAddr.getPageNumber(hugePageSize)); // 3MB / 2MB = 1
        assertEquals(1024 * 1024, vAddr.getOffset(hugePageSize)); // 3MB % 2MB = 1MB offset
    }
}
