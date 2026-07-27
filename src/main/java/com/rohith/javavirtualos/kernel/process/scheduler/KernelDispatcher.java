package com.rohith.javavirtualos.kernel.process.scheduler;

import com.rohith.javavirtualos.kernel.core.CPU;
import com.rohith.javavirtualos.kernel.core.CPUState;
import com.rohith.javavirtualos.kernel.core.KernelTick;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.SchedulerEvent;
import com.rohith.javavirtualos.kernel.metrics.ExecutionTimeline;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.state.ProcessState;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;

public class KernelDispatcher {
    private final CPU cpu;
    private final Scheduler scheduler;
    private final KernelTick tick;
    private final KernelEventBus eventBus;
    private final ExecutionTimeline timeline;
    private final int quantum;
    private int currentQuantumTicks = 0;

    public KernelDispatcher(CPU cpu, Scheduler scheduler, KernelTick tick, KernelEventBus eventBus, ExecutionTimeline timeline, int quantum) {
        this.cpu = cpu;
        this.scheduler = scheduler;
        this.tick = tick;
        this.eventBus = eventBus;
        this.timeline = timeline;
        this.quantum = quantum;
    }

    public synchronized void dispatch() {
        tick.increment();
        ProcessControlBlock current = cpu.getCurrentProcess();

        if (current != null) {
            current.getSchedulingInfo().getStatistics().incrementExecutionTime();
            timeline.record(tick.get(), current.getPid());
            currentQuantumTicks++;

            if (current.getSchedulingInfo().getBurstTime() > 0 && 
                current.getSchedulingInfo().getStatistics().getExecutionTime() >= current.getSchedulingInfo().getBurstTime()) {
                current.setState(ProcessState.TERMINATED);
                current.getSchedulingInfo().getStatistics().setTurnaroundTime(tick.get() - current.getSchedulingInfo().getArrivalTime());
                cpu.setCurrentProcess(null);
                cpu.setState(CPUState.IDLE);
                eventBus.publish(new SchedulerEvent(new ContextSwitch(current, null, tick.get(), ContextSwitch.Reason.PROCESS_FINISHED)));
            } else if (current.getTask() != null && !current.getTask().isRunning()) {
                current.setState(ProcessState.TERMINATED);
                current.getSchedulingInfo().getStatistics().setTurnaroundTime(tick.get() - current.getSchedulingInfo().getArrivalTime());
                cpu.setCurrentProcess(null);
                cpu.setState(CPUState.IDLE);
                eventBus.publish(new SchedulerEvent(new ContextSwitch(current, null, tick.get(), ContextSwitch.Reason.PROCESS_FINISHED)));
            } else if (quantum > 0 && currentQuantumTicks >= quantum) {
                current.setState(ProcessState.READY);
                scheduler.addProcess(current);
                cpu.setCurrentProcess(null);
                cpu.setState(CPUState.IDLE);
                eventBus.publish(new SchedulerEvent(new ContextSwitch(current, null, tick.get(), ContextSwitch.Reason.QUANTUM_EXPIRED)));
            }
        }

        if (cpu.getState() == CPUState.IDLE) {
            ProcessControlBlock next = scheduler.nextProcess();
            if (next != null) {
                ProcessControlBlock old = current;
                next.setState(ProcessState.RUNNING);
                cpu.setCurrentProcess(next);
                cpu.setState(CPUState.RUNNING);
                currentQuantumTicks = 0;
                
                next.getSchedulingInfo().getStatistics().incrementContextSwitchCount();
                if (next.getSchedulingInfo().getStatistics().getResponseTime() == -1) {
                    next.getSchedulingInfo().getStatistics().setResponseTime(tick.get() - next.getSchedulingInfo().getArrivalTime());
                }
                
                eventBus.publish(new SchedulerEvent(new ContextSwitch(old, next, tick.get(), ContextSwitch.Reason.INITIAL_DISPATCH)));
            }
        }
        
        for (ProcessControlBlock pcb : scheduler.getReadyQueue().getQueue()) {
            pcb.getSchedulingInfo().getStatistics().incrementWaitingTime();
        }
    }
}
