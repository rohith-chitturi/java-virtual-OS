package com.rohith.javavirtualos.kernel.process;

import com.rohith.javavirtualos.kernel.User;

public class VirtualProcess {
    private final int pid;
    private final String name;
    private final User owner;
    private final long startTime;
    private ProcessState state;
    private Thread executionThread;
    private final Runnable task;

    public VirtualProcess(int pid, String name, User owner, Runnable task) {
        this.pid = pid;
        this.name = name;
        this.owner = owner;
        this.task = task;
        this.state = ProcessState.NEW;
        this.startTime = System.currentTimeMillis();
    }

    public void start() {
        if (state != ProcessState.NEW) {
            return;
        }
        state = ProcessState.RUNNING;
        
        // Wrap the task to handle completion automatically
        Runnable wrappedTask = () -> {
            try {
                task.run();
            } finally {
                state = ProcessState.TERMINATED;
            }
        };

        executionThread = new Thread(wrappedTask, "VP-" + pid + "-" + name);
        executionThread.start();
    }

    public void kill() {
        if (state == ProcessState.RUNNING && executionThread != null) {
            executionThread.interrupt();
            state = ProcessState.TERMINATED;
        }
    }

    public int getPid() { return pid; }
    public String getName() { return name; }
    public User getOwner() { return owner; }
    public ProcessState getState() { return state; }
    public long getStartTime() { return startTime; }
}
