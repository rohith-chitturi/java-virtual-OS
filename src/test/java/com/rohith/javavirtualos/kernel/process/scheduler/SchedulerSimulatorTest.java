package com.rohith.javavirtualos.kernel.process.scheduler;

import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.kernel.core.CPU;
import com.rohith.javavirtualos.kernel.core.KernelTick;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.metrics.ExecutionTimeline;
import com.rohith.javavirtualos.kernel.process.manager.ProcessTask;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.scheduler.roundrobin.RoundRobinScheduler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchedulerSimulatorTest {

    @Test
    void testRoundRobin() {
        CPU cpu = new CPU(0);
        KernelTick tick = new KernelTick();
        KernelEventBus bus = new KernelEventBus();
        ExecutionTimeline timeline = new ExecutionTimeline();
        RoundRobinScheduler scheduler = new RoundRobinScheduler();
        
        KernelDispatcher dispatcher = new KernelDispatcher(cpu, scheduler, tick, bus, timeline, 2);
        
        User root = new User("root", "pwd");
        
        ProcessControlBlock p1 = new ProcessControlBlock(1, "P1", root, new ProcessTask(() -> {}), 1, 0);
        p1.getSchedulingInfo().setBurstTime(5);
        
        ProcessControlBlock p2 = new ProcessControlBlock(2, "P2", root, new ProcessTask(() -> {}), 1, 2);
        p2.getSchedulingInfo().setBurstTime(3);
        
        ProcessControlBlock p3 = new ProcessControlBlock(3, "P3", root, new ProcessTask(() -> {}), 1, 4);
        p3.getSchedulingInfo().setBurstTime(6);
        
        // Simulated execution loop
        for (int i = 0; i < 20; i++) {
            if (i == 0) scheduler.addProcess(p1);
            if (i == 2) scheduler.addProcess(p2);
            if (i == 4) scheduler.addProcess(p3);
            dispatcher.dispatch();
        }
        
        assertEquals(5, p1.getSchedulingInfo().getStatistics().getExecutionTime());
        assertEquals(3, p2.getSchedulingInfo().getStatistics().getExecutionTime());
        // p3 would have 6 execution time if it finished
    }
}
