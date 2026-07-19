package com.rohith.javavirtualos.kernel.resource;

import com.rohith.javavirtualos.kernel.exceptions.ResourceAllocationException;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;

public class ResourceManager {
    private final long maxMemory;
    private long allocatedMemory = 0;

    public ResourceManager(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public synchronized void allocateMemory(ProcessControlBlock pcb, long bytes) {
        if (allocatedMemory + bytes > maxMemory) {
            throw new ResourceAllocationException("Out of memory. Requested: " + bytes + ", Available: " + (maxMemory - allocatedMemory));
        }
        allocatedMemory += bytes;
        pcb.getResourceInfo().setMemoryUsage(bytes);
    }

    public synchronized void deallocateMemory(ProcessControlBlock pcb) {
        allocatedMemory -= pcb.getResourceInfo().getMemoryUsage();
        pcb.getResourceInfo().setMemoryUsage(0);
    }
}
