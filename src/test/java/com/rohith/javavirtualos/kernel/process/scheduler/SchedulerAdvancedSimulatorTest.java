package com.rohith.javavirtualos.kernel.process.scheduler;

import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.kernel.core.KernelTick;
import com.rohith.javavirtualos.kernel.core.MultiCoreProcessor;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.metrics.ExecutionTimeline;
import com.rohith.javavirtualos.kernel.process.manager.ProcessTask;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.pcb.SchedulingInfo;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;
import com.rohith.javavirtualos.kernel.scheduler.SchedulerStatistics;
import com.rohith.javavirtualos.kernel.scheduler.cfs.CompletelyFairScheduler;
import com.rohith.javavirtualos.kernel.scheduler.edf.EarliestDeadlineFirstScheduler;
import com.rohith.javavirtualos.kernel.scheduler.mlfq.MultiLevelFeedbackQueueScheduler;
import com.rohith.javavirtualos.kernel.scheduler.roundrobin.RoundRobinScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerAdvancedSimulatorTest {

    private KernelTick tick;
    private KernelEventBus bus;
    private ExecutionTimeline timeline;
    private SchedulerStatistics statistics;
    private User root;

    @BeforeEach
    void setUp() {
        tick = new KernelTick();
        bus = new KernelEventBus();
        timeline = new ExecutionTimeline();
        statistics = new SchedulerStatistics();
        root = new User("root", "pwd");
    }

    private ProcessControlBlock createProcess(int pid, int burstTime) {
        SchedulingInfo info = new SchedulingInfo(0, tick.get());
        info.setBurstTime(burstTime);
        ProcessTask task = new ProcessTask(() -> { /* dummy */ });
        return new ProcessControlBlock(pid, pid, pid, 1, "proc" + pid, root, task, info, null);
    }

    @Test
    void testMultiCoreExecution() {
        // Four runnable processes on four cores. Verify each core executes one process.
        MultiCoreProcessor processor = new MultiCoreProcessor(4);
        List<Scheduler> schedulers = new ArrayList<>();
        for (int i = 0; i < 4; i++) schedulers.add(new RoundRobinScheduler());
        
        KernelDispatcher dispatcher = new KernelDispatcher(processor, schedulers, tick, bus, timeline, 2, statistics);
        
        ProcessControlBlock p1 = createProcess(101, 10);
        ProcessControlBlock p2 = createProcess(102, 10);
        ProcessControlBlock p3 = createProcess(103, 10);
        ProcessControlBlock p4 = createProcess(104, 10);
        
        // Use affinity to strictly bind them for this deterministic test
        p1.getSchedulingInfo().setCpuAffinityMask(1L << 0);
        p2.getSchedulingInfo().setCpuAffinityMask(1L << 1);
        p3.getSchedulingInfo().setCpuAffinityMask(1L << 2);
        p4.getSchedulingInfo().setCpuAffinityMask(1L << 3);

        dispatcher.submitProcess(p1);
        dispatcher.submitProcess(p2);
        dispatcher.submitProcess(p3);
        dispatcher.submitProcess(p4);
        
        dispatcher.dispatch();
        
        // After one tick, all 4 cores should have a process running
        assertNotNull(processor.getCores().get(0).getCurrentProcess());
        assertEquals(101, processor.getCores().get(0).getCurrentProcess().getPid());
        assertEquals(102, processor.getCores().get(1).getCurrentProcess().getPid());
        assertEquals(103, processor.getCores().get(2).getCurrentProcess().getPid());
        assertEquals(104, processor.getCores().get(3).getCurrentProcess().getPid());
    }

    @Test
    void testAffinityPreventsMigration() {
        // Process pinned to Core 2. Verify it never migrates.
        MultiCoreProcessor processor = new MultiCoreProcessor(4);
        List<Scheduler> schedulers = new ArrayList<>();
        for (int i = 0; i < 4; i++) schedulers.add(new RoundRobinScheduler());
        
        KernelDispatcher dispatcher = new KernelDispatcher(processor, schedulers, tick, bus, timeline, 2, statistics);
        
        ProcessControlBlock p1 = createProcess(101, 20);
        p1.getSchedulingInfo().setCpuAffinityMask(1L << 2); // Core 2
        
        dispatcher.submitProcess(p1);
        
        for (int i = 0; i < 15; i++) {
            dispatcher.dispatch(); // will run load balancer at tick 10
            // Ensure p1 only ever runs on core 2
            assertNull(processor.getCores().get(0).getCurrentProcess());
            assertNull(processor.getCores().get(1).getCurrentProcess());
            assertNull(processor.getCores().get(3).getCurrentProcess());
        }
        
        assertEquals(101, processor.getCores().get(2).getCurrentProcess().getPid());
    }

    @Test
    void testLoadBalancer() {
        // Overloaded core 0, idle core 1. Verify migration.
        MultiCoreProcessor processor = new MultiCoreProcessor(2);
        List<Scheduler> schedulers = new ArrayList<>();
        schedulers.add(new RoundRobinScheduler());
        schedulers.add(new RoundRobinScheduler());
        
        KernelDispatcher dispatcher = new KernelDispatcher(processor, schedulers, tick, bus, timeline, 2, statistics);
        
        ProcessControlBlock p1 = createProcess(101, 20);
        ProcessControlBlock p2 = createProcess(102, 20);
        ProcessControlBlock p3 = createProcess(103, 20);
        
        // Manually overload core 0
        schedulers.get(0).addProcess(p1);
        schedulers.get(0).addProcess(p2);
        schedulers.get(0).addProcess(p3);
        
        assertEquals(3, schedulers.get(0).getReadyQueue().size());
        assertEquals(0, schedulers.get(1).getReadyQueue().size());
        
        // Dispatch up to 10 to trigger load balancer
        for (int i = 0; i <= 10; i++) {
            dispatcher.dispatch();
        }
        
        // After tick 10, load balancer should have moved a process to Core 1
        assertTrue(schedulers.get(1).getReadyQueue().size() > 0 || processor.getCores().get(1).getCurrentProcess() != null,
                   "Core 1 should have received a process via Load Balancer");
    }

    @Test
    void testCFSScheduler() {
        CompletelyFairScheduler cfs = new CompletelyFairScheduler();
        
        ProcessControlBlock p1 = createProcess(101, 10);
        p1.getSchedulingInfo().setVruntime(50); // High vruntime
        
        ProcessControlBlock p2 = createProcess(102, 10);
        p2.getSchedulingInfo().setVruntime(10); // Low vruntime
        
        cfs.addProcess(p1);
        cfs.addProcess(p2);
        
        // p2 should be picked first because of lower vruntime
        ProcessControlBlock first = cfs.nextProcess();
        assertNotNull(first);
        assertEquals(102, first.getPid());
    }

    @Test
    void testMLFQScheduler() {
        MultiLevelFeedbackQueueScheduler mlfq = new MultiLevelFeedbackQueueScheduler();
        
        ProcessControlBlock p1 = createProcess(101, 10);
        p1.getSchedulingInfo().setPriority(0); // Highest priority queue
        
        ProcessControlBlock p2 = createProcess(102, 10);
        p2.getSchedulingInfo().setPriority(1); // Lower priority queue
        
        mlfq.addProcess(p1);
        mlfq.addProcess(p2);
        
        ProcessControlBlock first = mlfq.nextProcess();
        assertEquals(101, first.getPid(), "Queue 0 should be selected first");
        
        // Since p1 was popped, its priority should demote to 1
        assertEquals(1, first.getSchedulingInfo().getPriority(), "Priority should be demoted");
        
        mlfq.addProcess(first); // Requeue p1 at priority 1
        // Now both p1 and p2 are in queue 1. Let's see who is next. It should be p2 as it was added first.
        ProcessControlBlock second = mlfq.nextProcess();
        assertEquals(102, second.getPid());
    }
}
