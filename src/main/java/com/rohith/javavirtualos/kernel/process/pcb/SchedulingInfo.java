package com.rohith.javavirtualos.kernel.process.pcb;

public class SchedulingInfo {
    private int priority;
    private long arrivalTime;
    private long burstTime;
    private long remainingTime;
    private long vruntime;
    private long deadline;
    private long cpuAffinityMask;
    private final ProcessStatistics statistics;

    public SchedulingInfo(int priority, long arrivalTime) {
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.burstTime = 0;
        this.remainingTime = 0;
        this.vruntime = 0;
        this.deadline = 0;
        this.cpuAffinityMask = -1L; // All cores allowed
        this.statistics = new ProcessStatistics();
    }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public long getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(long arrivalTime) { this.arrivalTime = arrivalTime; }
    
    public long getBurstTime() { return burstTime; }
    public void setBurstTime(long burstTime) { this.burstTime = burstTime; }
    
    public long getRemainingTime() { return remainingTime; }
    public void setRemainingTime(long remainingTime) { this.remainingTime = remainingTime; }
    
    public long getVruntime() { return vruntime; }
    public void setVruntime(long vruntime) { this.vruntime = vruntime; }
    
    public long getDeadline() { return deadline; }
    public void setDeadline(long deadline) { this.deadline = deadline; }
    
    public long getCpuAffinityMask() { return cpuAffinityMask; }
    public void setCpuAffinityMask(long cpuAffinityMask) { this.cpuAffinityMask = cpuAffinityMask; }
    
    public ProcessStatistics getStatistics() { return statistics; }
}
