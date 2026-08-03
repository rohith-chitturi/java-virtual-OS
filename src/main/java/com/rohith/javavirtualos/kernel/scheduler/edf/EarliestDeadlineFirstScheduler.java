package com.rohith.javavirtualos.kernel.scheduler.edf;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.scheduler.ReadyQueue;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;

import java.util.Comparator;
import java.util.PriorityQueue;

public class EarliestDeadlineFirstScheduler implements Scheduler {
    private final PriorityQueue<ProcessControlBlock> queue;
    private final ReadyQueue readOnlyView = new ReadyQueue();

    public EarliestDeadlineFirstScheduler() {
        this.queue = new PriorityQueue<>(Comparator.comparingLong(p -> p.getSchedulingInfo().getDeadline()));
    }

    @Override
    public synchronized void addProcess(ProcessControlBlock pcb) {
        if (!queue.contains(pcb)) {
            queue.add(pcb);
            readOnlyView.add(pcb);
        }
    }

    @Override
    public synchronized void removeProcess(ProcessControlBlock pcb) {
        queue.remove(pcb);
        readOnlyView.remove(pcb);
    }

    @Override
    public synchronized ProcessControlBlock nextProcess() {
        ProcessControlBlock pcb = queue.poll();
        if (pcb != null) {
            readOnlyView.remove(pcb);
        }
        return pcb;
    }

    @Override
    public ReadyQueue getReadyQueue() {
        return readOnlyView;
    }

    @Override
    public String getName() {
        return "EDF";
    }
}
