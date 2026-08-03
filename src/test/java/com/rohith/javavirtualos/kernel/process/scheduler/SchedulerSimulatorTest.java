package com.rohith.javavirtualos.kernel.process.scheduler;

import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.kernel.core.SingleCoreProcessor;
import com.rohith.javavirtualos.kernel.core.KernelTick;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.metrics.ExecutionTimeline;
import com.rohith.javavirtualos.kernel.process.manager.ProcessTask;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.pcb.SchedulingInfo;
import com.rohith.javavirtualos.kernel.scheduler.roundrobin.RoundRobinScheduler;
import com.rohith.javavirtualos.kernel.scheduler.SchedulerStatistics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchedulerSimulatorTest {

    @Test
    void testRoundRobin() {
        SingleCoreProcessor processor = new SingleCoreProcessor();
        KernelTick tick = new KernelTick();
        KernelEventBus bus = new KernelEventBus();
        ExecutionTimeline timeline = new ExecutionTimeline();
        RoundRobinScheduler scheduler = new RoundRobinScheduler();
        SchedulerStatistics statistics = new SchedulerStatistics();
        
        KernelDispatcher dispatcher = new KernelDispatcher(processor, java.util.Collections.singletonList(scheduler), 
                                                           tick, bus, timeline, 2, statistics);
        
        User root = new User("root", "pwd");
        
        ProcessControlBlock p1 = new ProcessControlBlock(1, 1, 1, 0, "P1", root, new ProcessTask(() -> {}), new SchedulingInfo(1, 0), null);
        p1.getSchedulingInfo().setBurstTime(5);
        
        ProcessControlBlock p2 = new ProcessControlBlock(2, 2, 2, 0, "P2", root, new ProcessTask(() -> {}), new SchedulingInfo(1, 2), null);
        p2.getSchedulingInfo().setBurstTime(3);
        
        ProcessControlBlock p3 = new ProcessControlBlock(3, 3, 3, 0, "P3", root, new ProcessTask(() -> {}), new SchedulingInfo(1, 4), null);
        p3.getSchedulingInfo().setBurstTime(6);
        
        // Simulated execution loop
        for (int i = 0; i < 20; i++) {
            if (i == 0) dispatcher.submitProcess(p1);
            if (i == 2) dispatcher.submitProcess(p2);
            if (i == 4) dispatcher.submitProcess(p3);
            dispatcher.dispatch();
        }
        
        assertEquals(5, p1.getSchedulingInfo().getStatistics().getExecutionTime());
        assertEquals(3, p2.getSchedulingInfo().getStatistics().getExecutionTime());
        // p3 would have 6 execution time if it finished
    }
}
