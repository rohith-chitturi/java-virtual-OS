package com.rohith.javavirtualos.kernel.memory;

import java.util.List;

public class MemoryValidator {
    
    public void validate(MemoryMap map, MemorySize totalRam) {
        List<MemoryBlock> blocks = map.getBlocks();
        if (blocks.isEmpty()) throw new IllegalStateException("Memory Map is empty");

        long currentOffset = 0;
        for (int i = 0; i < blocks.size(); i++) {
            MemoryBlock block = blocks.get(i);
            
            // Check overlaps / valid address ranges
            if (block.getStart().getAddress() != currentOffset) {
                throw new IllegalStateException("Hole detected in physical address space at " + block.getStart());
            }
            if (block.getSize().toBytes() <= 0) {
                throw new IllegalStateException("Invalid block size " + block.getSize() + " at " + block.getStart());
            }

            // Check adjacent free blocks are merged
            if (i < blocks.size() - 1) {
                MemoryBlock next = blocks.get(i+1);
                if (block.getState() == MemoryState.FREE && next.getState() == MemoryState.FREE) {
                    throw new IllegalStateException("Unmerged adjacent free blocks detected at " + block.getStart());
                }
            }
            currentOffset += block.getSize().toBytes();
        }

        // Total memory consistency
        if (currentOffset != totalRam.toBytes()) {
            throw new IllegalStateException("Total memory inconsistency: Expected " + totalRam + ", got " + MemorySize.ofBytes(currentOffset));
        }
    }
}
