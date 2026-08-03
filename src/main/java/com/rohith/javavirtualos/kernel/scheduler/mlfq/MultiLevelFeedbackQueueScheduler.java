package com.rohith.javavirtualos.kernel.scheduler.mlfq;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.scheduler.ReadyQueue;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;

import java.util.LinkedList;
import java.util.List;

public class MultiLevelFeedbackQueueScheduler implements Scheduler {
    private final int numQueues = 3;
    private final List<LinkedList<ProcessControlBlock>> queues;
    private final ReadyQueue combinedView = new ReadyQueue();

    public MultiLevelFeedbackQueueScheduler() {
        queues = new java.util.ArrayList<>();
        for (int i = 0; i < numQueues; i++) {
            queues.add(new LinkedList<>());
        }
    }

    @Override
    public synchronized void addProcess(ProcessControlBlock pcb) {
        int currentPriority = pcb.getSchedulingInfo().getPriority();
        // Assume priority 0 is highest, 2 is lowest
        if (currentPriority < 0 || currentPriority >= numQueues) {
            currentPriority = 0; // Default to highest if out of bounds
            pcb.getSchedulingInfo().setPriority(currentPriority);
        }
        
        if (!queues.get(currentPriority).contains(pcb)) {
            queues.get(currentPriority).add(pcb);
            combinedView.add(pcb);
        }
    }

    @Override
    public synchronized void removeProcess(ProcessControlBlock pcb) {
        for (LinkedList<ProcessControlBlock> q : queues) {
            q.remove(pcb);
        }
        combinedView.remove(pcb);
    }

    @Override
    public synchronized ProcessControlBlock nextProcess() {
        for (int i = 0; i < numQueues; i++) {
            if (!queues.get(i).isEmpty()) {
                ProcessControlBlock pcb = queues.get(i).poll();
                combinedView.remove(pcb);
                
                // If it used its full quantum, demote its priority for next time
                if (i < numQueues - 1) {
                    pcb.getSchedulingInfo().setPriority(i + 1);
                }
                return pcb;
            }
        }
        return null;
    }

    @Override
    public ReadyQueue getReadyQueue() {
        return combinedView;
    }

    @Override
    public String getName() {
        return "MLFQ";
    }
}
