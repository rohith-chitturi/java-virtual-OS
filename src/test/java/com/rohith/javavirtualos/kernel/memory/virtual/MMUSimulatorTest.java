package com.rohith.javavirtualos.kernel.memory.virtual;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.memory.*;
import com.rohith.javavirtualos.kernel.memory.virtual.strategy.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MMUSimulatorTest {

    @Test
    void testLRUReplacement() {
        KernelEventBus bus = new KernelEventBus();
        
        MemoryMap memoryMap = new MemoryMap(MemorySize.ofKB(16), MemorySize.ofKB(0));
        FrameTable frameTable = new FrameTable(memoryMap);
        BackingStore backingStore = new BackingStore();
        
        LRUStrategy lru = new LRUStrategy();
        PageFaultHandler handler = new PageFaultHandler(frameTable, backingStore, lru, bus);
        
        TLB tlb = new TLB(2);
        MMUStatistics stats = new MMUStatistics();
        MemoryManagementUnit mmu = new MemoryManagementUnit(tlb, handler, stats, bus);
        
        PageTable pt = new PageTable(1);
        
        for (long i = 0; i < 4; i++) {
            mmu.translate(new VirtualAddress(i * 4096), pt);
        }
        
        mmu.translate(new VirtualAddress(0), pt);
        
        mmu.translate(new VirtualAddress(4 * 4096), pt);
        
        assertEquals(5, stats.getPageFaults());
        assertEquals(PageState.SWAPPED_OUT, pt.getEntry(1).getState());
        assertEquals(PageState.IN_MEMORY, pt.getEntry(0).getState());
        assertEquals(PageState.IN_MEMORY, pt.getEntry(4).getState());
    }

    @Test
    void testFIFOReplacement() {
        KernelEventBus bus = new KernelEventBus();
        
        MemoryMap memoryMap = new MemoryMap(MemorySize.ofKB(16), MemorySize.ofKB(0));
        FrameTable frameTable = new FrameTable(memoryMap);
        BackingStore backingStore = new BackingStore();
        
        FIFOStrategy fifo = new FIFOStrategy();
        PageFaultHandler handler = new PageFaultHandler(frameTable, backingStore, fifo, bus);
        
        TLB tlb = new TLB(2);
        MMUStatistics stats = new MMUStatistics();
        MemoryManagementUnit mmu = new MemoryManagementUnit(tlb, handler, stats, bus);
        
        PageTable pt = new PageTable(1);
        
        for (long i = 0; i < 4; i++) {
            mmu.translate(new VirtualAddress(i * 4096), pt);
        }
        
        mmu.translate(new VirtualAddress(0), pt);
        
        mmu.translate(new VirtualAddress(4 * 4096), pt);
        
        assertEquals(5, stats.getPageFaults());
        assertEquals(PageState.SWAPPED_OUT, pt.getEntry(0).getState());
        assertEquals(PageState.IN_MEMORY, pt.getEntry(4).getState());
    }
}
