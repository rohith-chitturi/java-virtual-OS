package com.rohith.javavirtualos.kernel.events;

public class ProcessCreatedEvent extends KernelEvent {
    private final int pid;
    private final String processName;

    public ProcessCreatedEvent(int pid, String processName) {
        this.pid = pid;
        this.processName = processName;
    }

    @Override
    public String getMessage() {
        return String.format("ProcessCreated PID=%d Name=%s", pid, processName);
    }
}
