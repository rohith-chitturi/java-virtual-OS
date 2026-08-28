package com.rohith.javavirtualos.kernel.process.runtime;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks statistics related to virtual machine execution.
 */
public class RuntimeStatistics {
    private final AtomicLong executablesLoaded = new AtomicLong(0);
    private final AtomicLong instructionsExecuted = new AtomicLong(0);
    private final AtomicLong systemCallsInvoked = new AtomicLong(0);
    private final AtomicLong runtimeFaults = new AtomicLong(0);

    public void incrementExecutablesLoaded() { executablesLoaded.incrementAndGet(); }
    public void addInstructionsExecuted(long count) { instructionsExecuted.addAndGet(count); }
    public void incrementSystemCallsInvoked() { systemCallsInvoked.incrementAndGet(); }
    public void incrementRuntimeFaults() { runtimeFaults.incrementAndGet(); }

    public long getExecutablesLoaded() { return executablesLoaded.get(); }
    public long getInstructionsExecuted() { return instructionsExecuted.get(); }
    public long getSystemCallsInvoked() { return systemCallsInvoked.get(); }
    public long getRuntimeFaults() { return runtimeFaults.get(); }
}
