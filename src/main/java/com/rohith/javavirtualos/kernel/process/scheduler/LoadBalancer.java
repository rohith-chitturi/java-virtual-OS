package com.rohith.javavirtualos.kernel.process.scheduler;

import com.rohith.javavirtualos.kernel.core.Processor;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.ProcessMigratedEvent;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;

import java.util.List;

public class LoadBalancer {
    private final KernelEventBus eventBus;

    public LoadBalancer(KernelEventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Balances processes across multiple per-core schedulers.
     * Moves processes from the busiest core to the idlest core if the imbalance is significant,
     * while respecting cpuAffinityMask.
     */
    public synchronized void balance(Processor processor, List<Scheduler> coreSchedulers, long tick) {
        if (processor.getCoreCount() <= 1) return;

        // Find busiest and idlest core queues
        int maxQueueSize = -1;
        int minQueueSize = Integer.MAX_VALUE;
        int busiestCoreIndex = -1;
        int idlestCoreIndex = -1;

        for (int i = 0; i < coreSchedulers.size(); i++) {
            int size = coreSchedulers.get(i).getReadyQueue().size();
            if (size > maxQueueSize) {
                maxQueueSize = size;
                busiestCoreIndex = i;
            }
            if (size < minQueueSize) {
                minQueueSize = size;
                idlestCoreIndex = i;
            }
        }

        // Only migrate if there is a difference of at least 2 in queue sizes
        if (maxQueueSize - minQueueSize >= 2) {
            Scheduler busyScheduler = coreSchedulers.get(busiestCoreIndex);
            Scheduler idleScheduler = coreSchedulers.get(idlestCoreIndex);
            
            // Try to find a process that is allowed to run on idlestCoreIndex
            List<ProcessControlBlock> candidates = busyScheduler.getReadyQueue().getQueue();
            for (ProcessControlBlock pcb : candidates) {
                if ((pcb.getSchedulingInfo().getCpuAffinityMask() & (1L << idlestCoreIndex)) != 0) {
                    // Allowed!
                    busyScheduler.removeProcess(pcb);
                    idleScheduler.addProcess(pcb);
                    eventBus.publish(new ProcessMigratedEvent(pcb.getPid(), busiestCoreIndex, idlestCoreIndex, tick));
                    break; // Only move one process per balance tick to avoid thrashing
                }
            }
        }
    }
}
