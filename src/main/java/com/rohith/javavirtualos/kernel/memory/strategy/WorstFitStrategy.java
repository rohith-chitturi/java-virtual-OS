package com.rohith.javavirtualos.kernel.memory.strategy;

import com.rohith.javavirtualos.kernel.memory.MemoryBlock;
import com.rohith.javavirtualos.kernel.memory.MemoryMap;
import com.rohith.javavirtualos.kernel.memory.MemorySize;
import com.rohith.javavirtualos.kernel.memory.MemoryState;

import java.util.Comparator;
import java.util.Optional;

public class WorstFitStrategy implements AllocationStrategy {
    @Override
    public MemoryBlock allocate(MemoryMap map, MemorySize requestedSize, int pid, int allocationId) {
        Optional<MemoryBlock> worstFit = map.getBlocks().stream()
                .filter(b -> b.getState() == MemoryState.FREE && b.getSize().toBytes() >= requestedSize.toBytes())
                .max(Comparator.comparingLong(b -> b.getSize().toBytes()));
        
        if (worstFit.isPresent()) {
            MemoryBlock block = worstFit.get();
            map.splitBlock(block, requestedSize, pid, allocationId);
            return block;
        }
        return null;
    }
    @Override
    public String getName() { return "Worst Fit"; }
}
