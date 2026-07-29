package com.rohith.javavirtualos.kernel.memory.virtual;

import com.rohith.javavirtualos.kernel.memory.PhysicalAddress;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CowMemoryTest {

    @Test
    void testCowFaultExceptionThrownOnWriteProtectedPage() {
        VirtualAddress vAddr = new VirtualAddress(4096);
        Page page = new Page(1, 1);
        PageTableEntry pte = new PageTableEntry(page);
        pte.setValid(true);
        pte.setWriteProtected(true);
        
        Frame frame = new Frame(0, new PhysicalAddress(0));
        frame.incrementRefCount();
        frame.incrementRefCount(); // Shared
        pte.setFrame(frame);

        PageTable pt = new PageTable(1);
        pt.getAllEntries().put(1L, pte);

        // Simulation checking
        assertTrue(pte.isWriteProtected());
        assertEquals(2, frame.getRefCount());
        
        CowFaultException exception = new CowFaultException(vAddr, page);
        assertNotNull(exception);
        assertEquals(page, exception.getPage());
    }
}
