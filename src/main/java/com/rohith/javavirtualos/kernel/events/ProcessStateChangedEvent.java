package com.rohith.javavirtualos.kernel.events;

public class ProcessStateChangedEvent extends KernelEvent {
    private final int pid;
    private final String processName;
    private final String owner;
    private final String oldState;
    private final String newState;

    public ProcessStateChangedEvent(int pid, String processName, String owner, String oldState, String newState) {
        this.pid = pid;
        this.processName = processName;
        this.owner = owner;
        this.oldState = oldState;
        this.newState = newState;
    }

    public int getPid() { return pid; }
    public String getOldState() { return oldState; }
    public String getNewState() { return newState; }

    @Override
    public String getMessage() {
        return String.format("PID=%d User=%s Action=StateChange Old=%s New=%s", pid, owner, oldState, newState);
    }
}
