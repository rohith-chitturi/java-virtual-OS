package com.rohith.javavirtualos.kernel.process;

import com.rohith.javavirtualos.kernel.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessManager {
    
    private final Map<Integer, VirtualProcess> processes;
    private final AtomicInteger nextPid;

    public ProcessManager() {
        this.processes = new ConcurrentHashMap<>();
        this.nextPid = new AtomicInteger(1000);
    }

    public VirtualProcess spawnProcess(String name, User owner, Runnable task) {
        int pid = nextPid.getAndIncrement();
        VirtualProcess process = new VirtualProcess(pid, name, owner, task);
        processes.put(pid, process);
        process.start();
        return process;
    }

    public boolean killProcess(int pid, User requestor) {
        VirtualProcess process = processes.get(pid);
        if (process == null) {
            return false;
        }

        // Security check
        if (!"root".equals(requestor.getUsername()) && !process.getOwner().getUsername().equals(requestor.getUsername())) {
            throw new SecurityException("Permission denied to kill process " + pid);
        }

        process.kill();
        return true;
    }

    public List<VirtualProcess> listProcesses() {
        return new ArrayList<>(processes.values());
    }
}
