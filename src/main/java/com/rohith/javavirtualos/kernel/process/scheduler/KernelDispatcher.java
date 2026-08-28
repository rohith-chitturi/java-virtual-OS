package com.rohith.javavirtualos.kernel.process.scheduler;

import com.rohith.javavirtualos.kernel.core.CPU;
import com.rohith.javavirtualos.kernel.core.CPUState;
import com.rohith.javavirtualos.kernel.core.KernelTick;
import com.rohith.javavirtualos.kernel.core.Processor;
import com.rohith.javavirtualos.kernel.events.*;
import com.rohith.javavirtualos.kernel.metrics.ExecutionTimeline;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.pcb.ExitStatus;
import com.rohith.javavirtualos.kernel.process.state.ProcessState;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;
import com.rohith.javavirtualos.kernel.scheduler.SchedulerStatistics;
import com.rohith.javavirtualos.kernel.process.runtime.ExecutionResult;
import com.rohith.javavirtualos.kernel.process.runtime.syscall.SystemCallDispatcher;
import com.rohith.javavirtualos.kernel.process.runtime.syscall.SystemCallResult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class KernelDispatcher {
    private final Processor processor;
    private List<Scheduler> coreSchedulers;
    private final KernelTick tick;
    private final KernelEventBus eventBus;
    private final ExecutionTimeline timeline;
    private final int quantum;
    private final SchedulerStatistics statistics;
    private final LoadBalancer loadBalancer;
    private final SystemCallDispatcher syscallDispatcher;
    
    private final int[] currentQuantumTicks;
    private final Map<ProcessControlBlock, Long> sleepingProcesses = new HashMap<>();

    public KernelDispatcher(Processor processor, List<Scheduler> coreSchedulers, KernelTick tick, 
                            KernelEventBus eventBus, ExecutionTimeline timeline, int quantum, 
                            SchedulerStatistics statistics, SystemCallDispatcher syscallDispatcher) {
        if (processor.getCoreCount() != coreSchedulers.size()) {
            throw new IllegalArgumentException("Number of schedulers must match number of cores");
        }
        this.processor = processor;
        this.coreSchedulers = coreSchedulers;
        this.tick = tick;
        this.eventBus = eventBus;
        this.timeline = timeline;
        this.quantum = quantum;
        this.statistics = statistics;
        this.syscallDispatcher = syscallDispatcher;
        this.loadBalancer = new LoadBalancer(eventBus);
        this.currentQuantumTicks = new int[processor.getCoreCount()];
    }

    public synchronized void dispatch() {
        tick.increment();
        eventBus.publish(new SchedulerTickEvent(tick.get()));
        statistics.incrementSchedulerInvocations();

        wakeSleepingProcesses();

        // Process each core
        List<CPU> cores = processor.getCores();
        for (int i = 0; i < cores.size(); i++) {
            dispatchCore(i, cores.get(i), coreSchedulers.get(i));
        }
        
        // Load Balancing
        if (tick.get() % 10 == 0) {
            loadBalancer.balance(processor, coreSchedulers, tick.get());
        }
    }

    private void wakeSleepingProcesses() {
        Iterator<Map.Entry<ProcessControlBlock, Long>> it = sleepingProcesses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ProcessControlBlock, Long> entry = it.next();
            if (tick.get() >= entry.getValue()) {
                ProcessControlBlock pcb = entry.getKey();
                pcb.setState(ProcessState.READY);
                submitProcess(pcb);
                it.remove();
            }
        }
    }

    private void dispatchCore(int coreIndex, CPU cpu, Scheduler scheduler) {
        ProcessControlBlock current = cpu.getCurrentProcess();

        if (current != null) {
            current.getSchedulingInfo().getStatistics().incrementExecutionTime();
            statistics.addExecutionTime(1);
            timeline.record(tick.get(), current.getPid());
            currentQuantumTicks[coreIndex]++;

            boolean finished = false;
            boolean processYielded = false;
            boolean processSleeps = false;
            
            if (current.getVirtualMachine() != null) {
                ExecutionResult result = current.getVirtualMachine().step();
                switch (result.getType()) {
                    case INSTRUCTION_EXECUTED:
                        break;
                    case YIELD:
                        processYielded = true;
                        break;
                    case SLEEP:
                        current.setState(ProcessState.WAITING);
                        sleepingProcesses.put(current, tick.get() + result.getValue());
                        processSleeps = true;
                        break;
                    case SYSCALL:
                        SystemCallResult syscallRes = syscallDispatcher.dispatch(current, result.getSyscallRequest());
                        current.getVirtualMachine().setSystemCallResult(syscallRes);
                        break;
                    case EXIT:
                        current.setExitStatus(ExitStatus.normal(result.getValue()));
                        current.setState(ProcessState.ZOMBIE);
                        finished = true;
                        break;
                    case HALT:
                        current.setExitStatus(ExitStatus.normal(0));
                        current.setState(ProcessState.ZOMBIE);
                        finished = true;
                        break;
                    case FAULT:
                        current.setExitStatus(ExitStatus.signaled(1));
                        current.setState(ProcessState.ZOMBIE);
                        finished = true;
                        break;
                }
            } else if (current.getSchedulingInfo().getBurstTime() > 0 && 
                current.getSchedulingInfo().getStatistics().getExecutionTime() >= current.getSchedulingInfo().getBurstTime()) {
                current.setState(ProcessState.TERMINATED);
                finished = true;
                eventBus.publish(new SchedulerEvent(new ContextSwitch(current, null, tick.get(), ContextSwitch.Reason.PROCESS_FINISHED)));
            } else if (current.getTask() != null && !current.getTask().isRunning()) {
                current.setState(ProcessState.TERMINATED);
                finished = true;
                eventBus.publish(new SchedulerEvent(new ContextSwitch(current, null, tick.get(), ContextSwitch.Reason.PROCESS_FINISHED)));
            }
            
            if (processYielded || (!finished && !processSleeps && quantum > 0 && currentQuantumTicks[coreIndex] >= quantum)) {
                current.setState(ProcessState.READY);
                current.setActiveCore(-1);
                scheduler.addProcess(current);
                cpu.setCurrentProcess(null);
                cpu.setState(CPUState.IDLE);
                eventBus.publish(new CpuIdleEvent(cpu.getCoreId(), tick.get()));
                eventBus.publish(new SchedulerEvent(new ContextSwitch(current, null, tick.get(), ContextSwitch.Reason.QUANTUM_EXPIRED)));
            } else if (finished || processSleeps) {
                if (finished) {
                    long turnaround = tick.get() - current.getSchedulingInfo().getArrivalTime();
                    current.getSchedulingInfo().getStatistics().setTurnaroundTime(turnaround);
                    statistics.addTurnaroundTime(turnaround);
                }
                current.setActiveCore(-1);
                cpu.setCurrentProcess(null);
                cpu.setState(CPUState.IDLE);
                eventBus.publish(new CpuIdleEvent(cpu.getCoreId(), tick.get()));
            }
        }

        if (cpu.getState() == CPUState.IDLE) {
            statistics.addIdleTime(1);
            ProcessControlBlock next = scheduler.nextProcess();
            if (next != null) {
                ProcessControlBlock old = current;
                next.setState(ProcessState.RUNNING);
                next.setActiveCore(cpu.getCoreId());
                cpu.setCurrentProcess(next);
                cpu.setState(CPUState.RUNNING);
                eventBus.publish(new CpuBusyEvent(cpu.getCoreId(), tick.get()));
                currentQuantumTicks[coreIndex] = 0;
                
                next.getSchedulingInfo().getStatistics().incrementContextSwitchCount();
                statistics.incrementContextSwitches();
                
                if (next.getSchedulingInfo().getStatistics().getResponseTime() == -1) {
                    next.getSchedulingInfo().getStatistics().setResponseTime(tick.get() - next.getSchedulingInfo().getArrivalTime());
                }
                
                eventBus.publish(new SchedulerEvent(new ContextSwitch(old, next, tick.get(), ContextSwitch.Reason.INITIAL_DISPATCH)));
            }
        }
        
        for (ProcessControlBlock pcb : scheduler.getReadyQueue().getQueue()) {
            pcb.getSchedulingInfo().getStatistics().incrementWaitingTime();
            statistics.addWaitTime(1);
        }
    }
    
    public void submitProcess(ProcessControlBlock pcb) {
        for (int i = 0; i < coreSchedulers.size(); i++) {
            if ((pcb.getSchedulingInfo().getCpuAffinityMask() & (1L << i)) != 0) {
                coreSchedulers.get(i).addProcess(pcb);
                return;
            }
        }
        coreSchedulers.get(0).addProcess(pcb);
    }
    
    public Processor getProcessor() { return processor; }
    public List<Scheduler> getCoreSchedulers() { return coreSchedulers; }
    
    public synchronized void setSchedulers(List<Scheduler> newSchedulers) {
        if (newSchedulers.size() != processor.getCoreCount()) {
            throw new IllegalArgumentException("Number of schedulers must match core count.");
        }
        for (int i = 0; i < coreSchedulers.size(); i++) {
            Scheduler old = coreSchedulers.get(i);
            Scheduler newSch = newSchedulers.get(i);
            for (ProcessControlBlock pcb : old.getReadyQueue().getQueue()) {
                newSch.addProcess(pcb);
            }
        }
        this.coreSchedulers = newSchedulers;
    }
    
    public SchedulerStatistics getStatistics() { return statistics; }
}

