package com.rohith.javavirtualos.kernel.events;

import com.rohith.javavirtualos.kernel.memory.MemoryBlock;
import com.rohith.javavirtualos.kernel.memory.MemorySize;

public abstract class MemoryEvent extends KernelEvent { 

    public static class MemoryAllocatedEvent extends MemoryEvent {
        private final MemoryBlock block;
        public MemoryAllocatedEvent(MemoryBlock block) { this.block = block; }
        @Override public String getMessage() { return "Memory Allocated: PID " + block.getPid() + " Size " + block.getSize(); }
    }

    public static class MemoryReleasedEvent extends MemoryEvent {
        private final MemoryBlock block;
        public MemoryReleasedEvent(MemoryBlock block) { this.block = block; }
        @Override public String getMessage() { return "Memory Released: PID " + block.getPid() + " Size " + block.getSize(); }
    }

    public static class OutOfMemoryEvent extends MemoryEvent {
        private final int pid;
        private final MemorySize requested;
        public OutOfMemoryEvent(int pid, MemorySize requested) {
            this.pid = pid;
            this.requested = requested;
        }
        @Override public String getMessage() { return "OOM: PID " + pid + " failed to allocate " + requested; }
    }

    public static class TLBHitEvent extends MemoryEvent {
        private final com.rohith.javavirtualos.kernel.memory.virtual.VirtualAddress address;
        public TLBHitEvent(com.rohith.javavirtualos.kernel.memory.virtual.VirtualAddress address) { this.address = address; }
        @Override public String getMessage() { return "TLB Hit: " + address; }
    }

    public static class TLBMissEvent extends MemoryEvent {
        private final com.rohith.javavirtualos.kernel.memory.virtual.VirtualAddress address;
        public TLBMissEvent(com.rohith.javavirtualos.kernel.memory.virtual.VirtualAddress address) { this.address = address; }
        @Override public String getMessage() { return "TLB Miss: " + address; }
    }

    public static class PageFaultEvent extends MemoryEvent {
        private final com.rohith.javavirtualos.kernel.memory.virtual.VirtualAddress address;
        public PageFaultEvent(com.rohith.javavirtualos.kernel.memory.virtual.VirtualAddress address) { this.address = address; }
        @Override public String getMessage() { return "Page Fault: " + address; }
    }

    public static class PageLoadedEvent extends MemoryEvent {
        private final com.rohith.javavirtualos.kernel.memory.virtual.Page page;
        private final com.rohith.javavirtualos.kernel.memory.virtual.Frame frame;
        public PageLoadedEvent(com.rohith.javavirtualos.kernel.memory.virtual.Page page, com.rohith.javavirtualos.kernel.memory.virtual.Frame frame) { this.page = page; this.frame = frame; }
        @Override public String getMessage() { return "Page Loaded: " + page + " -> " + frame; }
    }

    public static class PageEvictedEvent extends MemoryEvent {
        private final com.rohith.javavirtualos.kernel.memory.virtual.Page page;
        private final com.rohith.javavirtualos.kernel.memory.virtual.Frame frame;
        public PageEvictedEvent(com.rohith.javavirtualos.kernel.memory.virtual.Page page, com.rohith.javavirtualos.kernel.memory.virtual.Frame frame) { this.page = page; this.frame = frame; }
        @Override public String getMessage() { return "Page Evicted: " + page + " from " + frame; }
    }

}
