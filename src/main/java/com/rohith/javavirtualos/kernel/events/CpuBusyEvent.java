package com.rohith.javavirtualos.kernel.events;

public class CpuBusyEvent extends KernelEvent {
    private final int coreId;
    private final long tick;

    public CpuBusyEvent(int coreId, long tick) {
        this.coreId = coreId;
        this.tick = tick;
    }

    public int getCoreId() { return coreId; }
    public long getTick() { return tick; }

    @Override
    public String getMessage() {
        return String.format("Tick=%d CPU Core %d is now BUSY", tick, coreId);
    }
}
