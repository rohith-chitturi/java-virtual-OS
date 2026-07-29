package com.rohith.javavirtualos.kernel.events;

public class CpuIdleEvent extends KernelEvent {
    private final int coreId;
    private final long tick;

    public CpuIdleEvent(int coreId, long tick) {
        this.coreId = coreId;
        this.tick = tick;
    }

    public int getCoreId() { return coreId; }
    public long getTick() { return tick; }

    @Override
    public String getMessage() {
        return String.format("Tick=%d CPU Core %d went IDLE", tick, coreId);
    }
}
