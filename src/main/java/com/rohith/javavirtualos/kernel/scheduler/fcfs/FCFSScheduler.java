package com.rohith.javavirtualos.kernel.scheduler.fcfs;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.scheduler.ReadyQueue;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;

public class FCFSScheduler implements Scheduler {
    private final ReadyQueue queue = new ReadyQueue();

    @Override
    public void addProcess(ProcessControlBlock pcb) {
        queue.add(pcb);
    }

    @Override
    public void removeProcess(ProcessControlBlock pcb) {
        queue.remove(pcb);
    }

    @Override
    public ProcessControlBlock nextProcess() {
        if (queue.isEmpty()) return null;
        ProcessControlBlock pcb = queue.getQueue().get(0);
        queue.remove(pcb);
        return pcb;
    }

    @Override
    public ReadyQueue getReadyQueue() {
        return queue;
    }

    @Override
    public String getName() {
        return "FCFS";
    }
}
