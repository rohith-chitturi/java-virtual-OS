package com.rohith.javavirtualos.kernel.memory;

public class MemoryBlock {
    private PhysicalAddress start;
    private MemorySize size;
    private MemoryState state;
    private int allocationId;
    private int pid;

    public MemoryBlock(PhysicalAddress start, MemorySize size, MemoryState state) {
        this.start = start;
        this.size = size;
        this.state = state;
        this.allocationId = -1;
        this.pid = -1;
    }

    public PhysicalAddress getStart() { return start; }
    public void setStart(PhysicalAddress start) { this.start = start; }

    public MemorySize getSize() { return size; }
    public void setSize(MemorySize size) { this.size = size; }

    public PhysicalAddress getEnd() { return start.plus(size); }

    public MemoryState getState() { return state; }
    public void setState(MemoryState state) { this.state = state; }

    public int getAllocationId() { return allocationId; }
    public void setAllocationId(int allocationId) { this.allocationId = allocationId; }

    public int getPid() { return pid; }
    public void setPid(int pid) { this.pid = pid; }
}
