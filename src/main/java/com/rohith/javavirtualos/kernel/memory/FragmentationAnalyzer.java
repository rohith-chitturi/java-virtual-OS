package com.rohith.javavirtualos.kernel.memory;

import java.util.List;
import java.util.stream.Collectors;

public class FragmentationAnalyzer {
    
    public double fragmentationPercentage(MemoryMap map) {
        MemorySize totalFree = map.getFreeMemory();
        if (totalFree.toBytes() == 0) return 0.0;
        
        MemorySize largestHole = largestHole(map);
        return ((double) (totalFree.toBytes() - largestHole.toBytes()) / totalFree.toBytes()) * 100.0;
    }

    public MemorySize largestHole(MemoryMap map) {
        return map.getBlocks().stream()
                .filter(b -> b.getState() == MemoryState.FREE)
                .map(MemoryBlock::getSize)
                .max(MemorySize::compareTo)
                .orElse(MemorySize.ofBytes(0));
    }

    public MemorySize smallestHole(MemoryMap map) {
        return map.getBlocks().stream()
                .filter(b -> b.getState() == MemoryState.FREE)
                .map(MemoryBlock::getSize)
                .min(MemorySize::compareTo)
                .orElse(MemorySize.ofBytes(0));
    }

    public int holeCount(MemoryMap map) {
        return (int) map.getBlocks().stream()
                .filter(b -> b.getState() == MemoryState.FREE)
                .count();
    }
}
