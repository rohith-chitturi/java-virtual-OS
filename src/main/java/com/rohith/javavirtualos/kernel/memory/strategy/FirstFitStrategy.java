package com.rohith.javavirtualos.kernel.memory.strategy;

import com.rohith.javavirtualos.kernel.memory.MemoryBlock;
import com.rohith.javavirtualos.kernel.memory.MemoryMap;
import com.rohith.javavirtualos.kernel.memory.MemorySize;
import com.rohith.javavirtualos.kernel.memory.MemoryState;

public class FirstFitStrategy implements AllocationStrategy {
    @Override
    public MemoryBlock allocate(MemoryMap map, MemorySize requestedSize, int pid, int allocationId) {
        for (MemoryBlock block : map.getBlocks()) {
            if (block.getState() == MemoryState.FREE && block.getSize().toBytes() >= requestedSize.toBytes()) {
                map.splitBlock(block, requestedSize, pid, allocationId);
                return block;
            }
        }
        return null;
    }
    @Override
    public String getName() { return "First Fit"; }
}
