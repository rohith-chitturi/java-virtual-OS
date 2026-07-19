package com.rohith.javavirtualos.kernel.memory;

public class MemoryStatistics {
    private final MemorySize totalMemory;
    private MemorySize usedMemory;
    private MemorySize freeMemory;
    private MemorySize reservedMemory;
    private int allocationCount;
    private int deallocationCount;
    private int oomCount;

    public MemoryStatistics(MemorySize totalMemory, MemorySize reservedMemory) {
        this.totalMemory = totalMemory;
        this.reservedMemory = reservedMemory;
        this.usedMemory = MemorySize.ofBytes(0);
        this.freeMemory = totalMemory.subtract(reservedMemory);
        this.allocationCount = 0;
        this.deallocationCount = 0;
        this.oomCount = 0;
    }
    
    public MemoryStatistics(MemoryStatistics other) {
        this.totalMemory = other.totalMemory;
        this.usedMemory = other.usedMemory;
        this.freeMemory = other.freeMemory;
        this.reservedMemory = other.reservedMemory;
        this.allocationCount = other.allocationCount;
        this.deallocationCount = other.deallocationCount;
        this.oomCount = other.oomCount;
    }

    public MemoryStatistics snapshot() {
        return new MemoryStatistics(this);
    }

    public MemorySize getTotalMemory() { return totalMemory; }
    public MemorySize getUsedMemory() { return usedMemory; }
    public MemorySize getFreeMemory() { return freeMemory; }
    public MemorySize getReservedMemory() { return reservedMemory; }
    public int getAllocationCount() { return allocationCount; }
    public int getDeallocationCount() { return deallocationCount; }
    public int getOomCount() { return oomCount; }

    public void recordAllocation(MemorySize size) {
        this.usedMemory = this.usedMemory.add(size);
        this.freeMemory = this.freeMemory.subtract(size);
        this.allocationCount++;
    }

    public void recordDeallocation(MemorySize size) {
        this.usedMemory = this.usedMemory.subtract(size);
        this.freeMemory = this.freeMemory.add(size);
        this.deallocationCount++;
    }

    public void recordOom() {
        this.oomCount++;
    }
}
