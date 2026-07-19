package com.rohith.javavirtualos.kernel.memory;

import java.util.ArrayList;
import java.util.List;

public class MemorySnapshot {
    private final long tick;
    private final List<MemoryBlock> blocks;
    private final MemoryStatistics statistics;

    public MemorySnapshot(long tick, List<MemoryBlock> blocks, MemoryStatistics statistics) {
        this.tick = tick;
        this.blocks = new ArrayList<>();
        // Deep copy the blocks for the snapshot
        for (MemoryBlock b : blocks) {
            MemoryBlock copy = new MemoryBlock(b.getStart(), b.getSize(), b.getState());
            copy.setAllocationId(b.getAllocationId());
            copy.setPid(b.getPid());
            this.blocks.add(copy);
        }
        // Assuming MemoryStatistics is immutable or we copy its state
        this.statistics = statistics.snapshot();
    }

    public long getTick() { return tick; }
    public List<MemoryBlock> getBlocks() { return blocks; }
    public MemoryStatistics getStatistics() { return statistics; }
}
