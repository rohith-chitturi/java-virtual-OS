package com.rohith.javavirtualos.kernel.memory;

import com.rohith.javavirtualos.kernel.core.KernelTick;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.memory.strategy.AllocationStrategy;
import com.rohith.javavirtualos.kernel.events.OutOfMemoryEvent;
import com.rohith.javavirtualos.kernel.events.MemoryAllocatedEvent;
import com.rohith.javavirtualos.kernel.events.MemoryReleasedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryManager {
    private final MemoryMap memoryMap;
    private final AllocationStrategy strategy;
    private final FragmentationAnalyzer analyzer;
    private final MemoryStatistics statistics;
    private final MemoryValidator validator;
    private final KernelEventBus eventBus;
    private final KernelTick tick;
    
    private final List<MemoryAllocationRecord> history = new ArrayList<>();
    private final List<MemorySnapshot> snapshots = new ArrayList<>();
    private int allocationIdCounter = 0;

    public MemoryManager(MemorySize totalSize, MemorySize reservedKernelSize, AllocationStrategy strategy, KernelEventBus eventBus, KernelTick tick) {
        this.memoryMap = new MemoryMap(totalSize, reservedKernelSize);
        this.strategy = strategy;
        this.analyzer = new FragmentationAnalyzer();
        this.statistics = new MemoryStatistics(totalSize, reservedKernelSize);
        this.validator = new MemoryValidator();
        this.eventBus = eventBus;
        this.tick = tick;
        
        validator.validate(memoryMap, totalSize);
    }

    public synchronized MemoryBlock allocate(MemorySize requestedSize, int pid) {
        int allocId = ++allocationIdCounter;
        MemoryBlock block = strategy.allocate(memoryMap, requestedSize, pid, allocId);
        
        if (block != null) {
            statistics.recordAllocation(requestedSize);
            history.add(new MemoryAllocationRecord(allocId, pid, block.getStart(), requestedSize, tick.get(), strategy.getName()));
            validator.validate(memoryMap, statistics.getTotalMemory());
            eventBus.publish(new MemoryAllocatedEvent(block));
            return block;
        } else {
            statistics.recordOom();
            eventBus.publish(new OutOfMemoryEvent(pid, requestedSize));
            throw new RuntimeException("Out of Memory");
        }
    }

    public synchronized void free(int allocationId) {
        Optional<MemoryBlock> target = memoryMap.getBlocks().stream()
                .filter(b -> b.getAllocationId() == allocationId && b.getState() == MemoryState.ALLOCATED)
                .findFirst();

        if (target.isPresent()) {
            MemoryBlock block = target.get();
            MemorySize freedSize = block.getSize();
            block.setState(MemoryState.FREE);
            block.setAllocationId(-1);
            block.setPid(-1);
            
            memoryMap.mergeFreeBlocks();
            statistics.recordDeallocation(freedSize);
            validator.validate(memoryMap, statistics.getTotalMemory());
            eventBus.publish(new MemoryReleasedEvent(block));
        } else {
            throw new IllegalArgumentException("Allocation ID not found: " + allocationId);
        }
    }
    
    public synchronized void captureSnapshot() {
        snapshots.add(new MemorySnapshot(tick.get(), memoryMap.getBlocks(), statistics));
    }

    public MemoryMap getMemoryMap() { return memoryMap; }
    public MemoryStatistics getStatistics() { return statistics; }
    public FragmentationAnalyzer getAnalyzer() { return analyzer; }
    public String getStrategyName() { return strategy.getName(); }
    public List<MemoryAllocationRecord> getHistory() { return new ArrayList<>(history); }
    public MemoryValidator getValidator() { return validator; }
}
