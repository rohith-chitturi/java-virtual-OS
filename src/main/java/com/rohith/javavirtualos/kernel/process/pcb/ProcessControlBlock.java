package com.rohith.javavirtualos.kernel.process.pcb;

import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.kernel.process.manager.ProcessTask;
import com.rohith.javavirtualos.kernel.process.state.ProcessState;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.rohith.javavirtualos.kernel.ipc.Signal;

public class ProcessControlBlock {
    private final int pid;
    private final int tgid;
    private int pgid;
    private int parentPid;
    private final List<Integer> childrenPids;
    private ProcessState state;
    private int ownerUid;
    private final Queue<Signal> pendingSignals;
    private final ProcessTask task;
    private Thread thread;
    private final SchedulingInfo schedulingInfo;
    private final ResourceInfo resourceInfo;
    private final User owner;
    private final String commandName;
    private long startTime;
    private long endTime;
    private ExitStatus exitStatus;
    private int activeCore = -1;

    public ProcessControlBlock(int pid, int tgid, int pgid, int parentPid, String commandName, User owner, ProcessTask task, SchedulingInfo scheduling, ResourceInfo resource) {
        this.pid = pid;
        this.tgid = tgid;
        this.pgid = pgid;
        this.parentPid = parentPid;
        this.childrenPids = new ArrayList<>();
        this.commandName = commandName;
        this.owner = owner;
        this.task = task;
        this.schedulingInfo = scheduling;
        this.resourceInfo = resource;
        this.state = ProcessState.NEW;
        this.pendingSignals = new ConcurrentLinkedQueue<>();
    }

    public void addChild(int childPid) {
        childrenPids.add(childPid);
    }
    
    public void removeChild(int childPid) {
        childrenPids.remove(Integer.valueOf(childPid));
    }
    
    public int getPid() { return pid; }
    public int getTgid() { return tgid; }
    public int getPgid() { return pgid; }
    public void setPgid(int pgid) { this.pgid = pgid; }
    public int getParentPid() { return parentPid; }
    public void setParentPid(int parentPid) { this.parentPid = parentPid; }
    public List<Integer> getChildrenPids() { return childrenPids; }
    public ProcessState getState() { return state; }
    public void setState(ProcessState state) { this.state = state; }
    public int getOwnerUid() { return ownerUid; }
    public void setOwnerUid(int ownerUid) { this.ownerUid = ownerUid; }

    public void enqueueSignal(Signal signal) {
        pendingSignals.offer(signal);
    }
    
    public Signal dequeueSignal() {
        return pendingSignals.poll();
    }
    
    public boolean hasPendingSignals() {
        return !pendingSignals.isEmpty();
    }

    public ProcessTask getTask() { return task; }
    public Thread getThread() { return thread; }
    public void setThread(Thread thread) { this.thread = thread; }
    public SchedulingInfo getSchedulingInfo() { return schedulingInfo; }
    public ResourceInfo getResourceInfo() { return resourceInfo; }
    public User getOwner() { return owner; }
    public String getCommandName() { return commandName; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public ExitStatus getExitStatus() { return exitStatus; }
    public void setExitStatus(ExitStatus exitStatus) { this.exitStatus = exitStatus; }
    public int getActiveCore() { return activeCore; }
    public void setActiveCore(int activeCore) { this.activeCore = activeCore; }
}
