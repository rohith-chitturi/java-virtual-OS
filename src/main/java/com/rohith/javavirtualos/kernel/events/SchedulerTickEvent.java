package com.rohith.javavirtualos.kernel.events;

public class SchedulerTickEvent extends KernelEvent {
    private final long tick;

    public SchedulerTickEvent(long tick) {
        this.tick = tick;
    }

    public long getTick() { return tick; }

    @Override
    public String getMessage() {
        return String.format("Tick=%d Scheduler Tick", tick);
    }
}
