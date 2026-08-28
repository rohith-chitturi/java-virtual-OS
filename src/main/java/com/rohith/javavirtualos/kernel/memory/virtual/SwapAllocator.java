package com.rohith.javavirtualos.kernel.memory.virtual;

import java.util.BitSet;

public class SwapAllocator {
    private final int totalSlots;
    private final BitSet usedSlots;
    private int allocatedCount;

    public SwapAllocator(int totalSlots) {
        this.totalSlots = totalSlots;
        this.usedSlots = new BitSet(totalSlots);
        this.allocatedCount = 0;
    }

    public synchronized int allocate() {
        if (allocatedCount >= totalSlots) {
            return -1; // Out of swap space
        }
        int slot = usedSlots.nextClearBit(0);
        usedSlots.set(slot);
        allocatedCount++;
        return slot;
    }

    public synchronized void free(int slot) {
        if (slot >= 0 && slot < totalSlots && usedSlots.get(slot)) {
            usedSlots.clear(slot);
            allocatedCount--;
        }
    }

    public synchronized int getAllocatedCount() {
        return allocatedCount;
    }

    public int getTotalSlots() {
        return totalSlots;
    }
}
