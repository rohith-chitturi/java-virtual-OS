package com.rohith.javavirtualos.kernel.scheduler;

public class SchedulerStatistics {
    private long contextSwitches = 0;
    private long queueMigrations = 0;
    private long totalWaitTime = 0;
    private long totalTurnaroundTime = 0;
    private long totalExecutionTime = 0;
    private long totalIdleTime = 0;
    private long schedulerInvocations = 0;
    
    public synchronized void incrementContextSwitches() { contextSwitches++; }
    public synchronized void incrementQueueMigrations() { queueMigrations++; }
    public synchronized void incrementSchedulerInvocations() { schedulerInvocations++; }
    public synchronized void addWaitTime(long time) { totalWaitTime += time; }
    public synchronized void addTurnaroundTime(long time) { totalTurnaroundTime += time; }
    public synchronized void addExecutionTime(long time) { totalExecutionTime += time; }
    public synchronized void addIdleTime(long time) { totalIdleTime += time; }

    public long getContextSwitches() { return contextSwitches; }
    public long getQueueMigrations() { return queueMigrations; }
    public long getTotalWaitTime() { return totalWaitTime; }
    public long getTotalTurnaroundTime() { return totalTurnaroundTime; }
    public long getTotalExecutionTime() { return totalExecutionTime; }
    public long getTotalIdleTime() { return totalIdleTime; }
    public long getSchedulerInvocations() { return schedulerInvocations; }
}
