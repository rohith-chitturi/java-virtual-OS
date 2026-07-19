package com.rohith.javavirtualos.kernel.memory.strategy;

import com.rohith.javavirtualos.kernel.memory.MemoryMap;

public interface CompactionStrategy {
    void compact(MemoryMap map);
}
