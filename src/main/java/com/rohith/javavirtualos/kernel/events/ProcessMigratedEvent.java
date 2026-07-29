package com.rohith.javavirtualos.kernel.events;

public class ProcessMigratedEvent extends KernelEvent {
    private final int pid;
    private final int fromCoreId;
    private final int toCoreId;
    private final long tick;

    public ProcessMigratedEvent(int pid, int fromCoreId, int toCoreId, long tick) {
        this.pid = pid;
        this.fromCoreId = fromCoreId;
        this.toCoreId = toCoreId;
        this.tick = tick;
    }

    public int getPid() { return pid; }
    public int getFromCoreId() { return fromCoreId; }
    public int getToCoreId() { return toCoreId; }
    public long getTick() { return tick; }

    @Override
    public String getMessage() {
        return String.format("Tick=%d Migration: PID %d migrated from Core %d to Core %d", 
                tick, pid, fromCoreId, toCoreId);
    }
}
