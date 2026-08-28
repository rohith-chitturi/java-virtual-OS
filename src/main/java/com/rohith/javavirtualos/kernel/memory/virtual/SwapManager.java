package com.rohith.javavirtualos.kernel.memory.virtual;


import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.MemoryEvent.SwapInEvent;
import com.rohith.javavirtualos.kernel.events.MemoryEvent.SwapOutEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SwapManager {
    private final SwapAllocator allocator;

    private final KernelEventBus eventBus;
    private final Map<Page, Integer> pageSwapMap; // Maps Page to Swap Slot

    public SwapManager(SwapAllocator allocator, KernelEventBus eventBus) {
        this.allocator = allocator;
        this.eventBus = eventBus;
        this.pageSwapMap = new ConcurrentHashMap<>();
    }

    public boolean store(Page page) {
        int slot = allocator.allocate();
        if (slot == -1) {
            return false; // Swap space full
        }
        
        // In a real system, we'd write to the swapfile using the slot offset
        // fsManager.resolvePath("/swapfile") ...
        
        pageSwapMap.put(page, slot);
        eventBus.publish(new SwapOutEvent(page));
        return true;
    }

    public boolean load(Page page) {
        Integer slot = pageSwapMap.get(page);
        if (slot == null) {
            return false;
        }
        
        // In a real system, we'd read from the swapfile using the slot offset
        
        allocator.free(slot);
        pageSwapMap.remove(page);
        eventBus.publish(new SwapInEvent(page));
        return true;
    }

    public void remove(Page page) {
        Integer slot = pageSwapMap.remove(page);
        if (slot != null) {
            allocator.free(slot);
        }
    }
    
    public int getUsage() {
        return allocator.getAllocatedCount();
    }
    
    public int getTotalSlots() {
        return allocator.getTotalSlots();
    }
}
