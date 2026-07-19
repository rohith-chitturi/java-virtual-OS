package com.rohith.javavirtualos.kernel.process.pcb;

public class ProcessStatistics {
    private long waitingTime = 0;
    private long turnaroundTime = 0;
    private long responseTime = -1; // -1 indicates not yet responded
    private long executionTime = 0;
    private int contextSwitchCount = 0;

    public long getWaitingTime() { return waitingTime; }
    public void incrementWaitingTime() { this.waitingTime++; }

    public long getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(long turnaroundTime) { this.turnaroundTime = turnaroundTime; }

    public long getResponseTime() { return responseTime; }
    public void setResponseTime(long responseTime) { this.responseTime = responseTime; }

    public long getExecutionTime() { return executionTime; }
    public void incrementExecutionTime() { this.executionTime++; }

    public int getContextSwitchCount() { return contextSwitchCount; }
    public void incrementContextSwitchCount() { this.contextSwitchCount++; }
}
