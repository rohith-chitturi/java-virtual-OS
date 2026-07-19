package com.rohith.javavirtualos.kernel.memory;

public class MemoryAllocationRecord {
    private final int allocationId;
    private final int pid;
    private final PhysicalAddress address;
    private final MemorySize size;
    private final long tick;
    private final String strategyName;

    public MemoryAllocationRecord(int allocationId, int pid, PhysicalAddress address, MemorySize size, long tick, String strategyName) {
        this.allocationId = allocationId;
        this.pid = pid;
        this.address = address;
        this.size = size;
        this.tick = tick;
        this.strategyName = strategyName;
    }

    public int getAllocationId() { return allocationId; }
    public int getPid() { return pid; }
    public PhysicalAddress getAddress() { return address; }
    public MemorySize getSize() { return size; }
    public long getTick() { return tick; }
    public String getStrategyName() { return strategyName; }
}
