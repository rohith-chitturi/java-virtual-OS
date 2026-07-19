package com.rohith.javavirtualos.kernel.events;

import com.rohith.javavirtualos.kernel.memory.MemoryBlock;
import com.rohith.javavirtualos.kernel.memory.MemorySize;

public abstract class MemoryEvent extends KernelEvent { }

public class MemoryAllocatedEvent extends MemoryEvent {
    private final MemoryBlock block;
    public MemoryAllocatedEvent(MemoryBlock block) { this.block = block; }
    @Override public String getMessage() { return "Memory Allocated: PID " + block.getPid() + " Size " + block.getSize(); }
}

public class MemoryReleasedEvent extends MemoryEvent {
    private final MemoryBlock block;
    public MemoryReleasedEvent(MemoryBlock block) { this.block = block; }
    @Override public String getMessage() { return "Memory Released: PID " + block.getPid() + " Size " + block.getSize(); }
}

public class OutOfMemoryEvent extends MemoryEvent {
    private final int pid;
    private final MemorySize requested;
    public OutOfMemoryEvent(int pid, MemorySize requested) {
        this.pid = pid;
        this.requested = requested;
    }
    @Override public String getMessage() { return "OOM: PID " + pid + " failed to allocate " + requested; }
}
