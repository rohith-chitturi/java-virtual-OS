package com.rohith.javavirtualos.kernel.scheduler.priority;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.scheduler.ReadyQueue;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;

import java.util.Comparator;
import java.util.List;

public class PriorityScheduler implements Scheduler {
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
        List<ProcessControlBlock> list = queue.getQueue();
        // Lowest number = highest priority
        list.sort(Comparator.comparingInt(p -> p.getSchedulingInfo().getPriority()));
        ProcessControlBlock pcb = list.get(0);
        queue.remove(pcb);
        return pcb;
    }

    @Override
    public ReadyQueue getReadyQueue() {
        return queue;
    }

    @Override
    public String getName() {
        return "Priority";
    }
}
