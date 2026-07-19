package com.rohith.javavirtualos.kernel.process.pcb;

public class SchedulingInfo {
    private int priority;
    private long arrivalTime;
    private long burstTime;
    private long remainingTime;
    private long waitingTime;
    private long turnAroundTime;

    public SchedulingInfo(int priority, long arrivalTime) {
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.burstTime = 0;
        this.remainingTime = 0;
        this.waitingTime = 0;
        this.turnAroundTime = 0;
    }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public long getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(long arrivalTime) { this.arrivalTime = arrivalTime; }
    
    public long getBurstTime() { return burstTime; }
    public void setBurstTime(long burstTime) { this.burstTime = burstTime; }
    
    public long getRemainingTime() { return remainingTime; }
    public void setRemainingTime(long remainingTime) { this.remainingTime = remainingTime; }
    
    public long getWaitingTime() { return waitingTime; }
    public void setWaitingTime(long waitingTime) { this.waitingTime = waitingTime; }
    
    public long getTurnAroundTime() { return turnAroundTime; }
    public void setTurnAroundTime(long turnAroundTime) { this.turnAroundTime = turnAroundTime; }
}
