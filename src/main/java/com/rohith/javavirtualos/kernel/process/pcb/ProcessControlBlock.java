package com.rohith.javavirtualos.kernel.process.pcb;

import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.kernel.process.manager.ProcessTask;
import com.rohith.javavirtualos.kernel.process.state.ProcessState;

import java.util.ArrayList;
import java.util.List;

public class ProcessControlBlock {
    private final int pid;
    private final int parentPid;
    private final List<Integer> childrenPids;
    private ProcessState state;
    private final ProcessTask task;
    private Thread thread;
    private final SchedulingInfo schedulingInfo;
    private final ResourceInfo resourceInfo;
    private final User owner;
    private final String commandName;
    private long startTime;
    private long endTime;

    public ProcessControlBlock(int pid, int parentPid, String commandName, User owner, ProcessTask task, SchedulingInfo scheduling, ResourceInfo resource) {
        this.pid = pid;
        this.parentPid = parentPid;
        this.childrenPids = new ArrayList<>();
        this.commandName = commandName;
        this.owner = owner;
        this.task = task;
        this.schedulingInfo = scheduling;
        this.resourceInfo = resource;
        this.state = ProcessState.NEW;
    }

    public void addChild(int childPid) {
        childrenPids.add(childPid);
    }
    
    public int getPid() { return pid; }
    public int getParentPid() { return parentPid; }
    public List<Integer> getChildrenPids() { return childrenPids; }
    public ProcessState getState() { return state; }
    public void setState(ProcessState state) { this.state = state; }
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
}
