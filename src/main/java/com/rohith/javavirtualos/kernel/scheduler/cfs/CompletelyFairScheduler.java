package com.rohith.javavirtualos.kernel.scheduler.cfs;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.scheduler.ReadyQueue;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;

import java.util.Comparator;
import java.util.PriorityQueue;

public class CompletelyFairScheduler implements Scheduler {
    // We use a PriorityQueue to simulate the red-black tree used in actual Linux CFS.
    // It's ordered by virtual runtime (vruntime).
    private final PriorityQueue<ProcessControlBlock> queue;
    private final ReadyQueue readOnlyView = new ReadyQueue();

    public CompletelyFairScheduler() {
        this.queue = new PriorityQueue<>(Comparator.comparingLong(p -> p.getSchedulingInfo().getVruntime()));
    }

    @Override
    public synchronized void addProcess(ProcessControlBlock pcb) {
        if (!queue.contains(pcb)) {
            // Give newly arrived processes the minimum vruntime in the queue to prevent starvation
            if (!queue.isEmpty() && pcb.getSchedulingInfo().getVruntime() == 0) {
                long minVruntime = queue.peek().getSchedulingInfo().getVruntime();
                pcb.getSchedulingInfo().setVruntime(minVruntime);
            }
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
            // Simulate vruntime increment. In a real CFS, this increments based on actual execution time.
            // We increment it slightly when popped so it cycles fairly if requeued.
            pcb.getSchedulingInfo().setVruntime(pcb.getSchedulingInfo().getVruntime() + 1);
        }
        return pcb;
    }

    @Override
    public ReadyQueue getReadyQueue() {
        return readOnlyView;
    }

    @Override
    public String getName() {
        return "CFS";
    }
}
