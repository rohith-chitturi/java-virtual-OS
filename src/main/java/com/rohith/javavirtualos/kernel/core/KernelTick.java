package com.rohith.javavirtualos.kernel.core;

/**
 * Defines a logical unit of time in the OS.
 * 
 * 1 Kernel Tick =
 * • Dispatcher executes once
 * • Scheduler makes at most one decision
 * • Running process executes one logical time unit
 * • Metrics update
 * • Events are published
 */
public class KernelTick {
    private long tick;

    public KernelTick() {
        this.tick = 0;
    }

    public void increment() {
        tick++;
    }

    public long get() {
        return tick;
    }
}
