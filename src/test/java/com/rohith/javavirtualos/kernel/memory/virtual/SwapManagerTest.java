package com.rohith.javavirtualos.kernel.memory.virtual;

import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SwapManagerTest {

    private SwapManager swapManager;
    private SwapAllocator allocator;
    
    @BeforeEach
    void setUp() {
        allocator = new SwapAllocator(10);
        FileSystemManager fsManager = new FileSystemManager();
        KernelEventBus eventBus = new KernelEventBus();
        swapManager = new SwapManager(allocator, eventBus);
    }

    @Test
    void testSwapStoreAndLoad() {
        Page page = new Page(1, 100);
        
        assertTrue(swapManager.store(page));
        assertEquals(1, swapManager.getUsage());
        
        assertTrue(swapManager.load(page));
        assertEquals(0, swapManager.getUsage());
        
        // Cannot load something not in swap
        assertFalse(swapManager.load(page));
    }
    
    @Test
    void testSwapFull() {
        for (int i = 0; i < 10; i++) {
            assertTrue(swapManager.store(new Page(1, i)));
        }
        
        assertEquals(10, swapManager.getUsage());
        assertFalse(swapManager.store(new Page(1, 11))); // Should be full
    }
}
