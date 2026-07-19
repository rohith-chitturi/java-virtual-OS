package com.rohith.javavirtualos.kernel.memory.strategy;

import com.rohith.javavirtualos.kernel.memory.MemoryBlock;
import com.rohith.javavirtualos.kernel.memory.MemoryMap;
import com.rohith.javavirtualos.kernel.memory.MemorySize;
import com.rohith.javavirtualos.kernel.memory.MemoryState;

import java.util.Comparator;
import java.util.Optional;

public class BestFitStrategy implements AllocationStrategy {
    @Override
    public MemoryBlock allocate(MemoryMap map, MemorySize requestedSize, int pid, int allocationId) {
        Optional<MemoryBlock> bestFit = map.getBlocks().stream()
                .filter(b -> b.getState() == MemoryState.FREE && b.getSize().toBytes() >= requestedSize.toBytes())
                .min(Comparator.comparingLong(b -> b.getSize().toBytes()));
        
        if (bestFit.isPresent()) {
            MemoryBlock block = bestFit.get();
            map.splitBlock(block, requestedSize, pid, allocationId);
            return block;
        }
        return null;
    }
    @Override
    public String getName() { return "Best Fit"; }
}
