package com.rohith.javavirtualos.kernel.memory.strategy;

import com.rohith.javavirtualos.kernel.memory.MemoryBlock;
import com.rohith.javavirtualos.kernel.memory.MemoryMap;
import com.rohith.javavirtualos.kernel.memory.MemorySize;

public interface AllocationStrategy {
    MemoryBlock allocate(MemoryMap map, MemorySize requestedSize, int pid, int allocationId);
    String getName();
}
