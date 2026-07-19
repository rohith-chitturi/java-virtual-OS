package com.rohith.javavirtualos.kernel.metrics;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class KernelMetrics {
    private final AtomicInteger totalProcessesCreated = new AtomicInteger(0);
    private final AtomicInteger runningProcesses = new AtomicInteger(0);
    private final AtomicLong totalTurnAroundTime = new AtomicLong(0);
    private final AtomicLong peakMemoryUsage = new AtomicLong(0);

    public void incrementProcessesCreated() {
        totalProcessesCreated.incrementAndGet();
    }

    public void incrementRunningProcesses() {
        runningProcesses.incrementAndGet();
    }

    public void decrementRunningProcesses() {
        runningProcesses.decrementAndGet();
    }

    public void recordMemoryUsage(long usage) {
        long currentPeak;
        do {
            currentPeak = peakMemoryUsage.get();
            if (usage <= currentPeak) {
                break;
            }
        } while (!peakMemoryUsage.compareAndSet(currentPeak, usage));
    }

    public int getTotalProcessesCreated() { return totalProcessesCreated.get(); }
    public int getRunningProcesses() { return runningProcesses.get(); }
    public long getPeakMemoryUsage() { return peakMemoryUsage.get(); }
}
