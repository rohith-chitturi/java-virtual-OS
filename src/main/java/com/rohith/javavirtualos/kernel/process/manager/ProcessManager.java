package com.rohith.javavirtualos.kernel.process.manager;

import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.kernel.core.KernelConfig;
import com.rohith.javavirtualos.kernel.core.PIDGenerator;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.ProcessCreatedEvent;
import com.rohith.javavirtualos.kernel.events.ProcessStateChangedEvent;
import com.rohith.javavirtualos.kernel.exceptions.PermissionDeniedException;
import com.rohith.javavirtualos.kernel.exceptions.ProcessNotFoundException;
import com.rohith.javavirtualos.kernel.exceptions.ResourceAllocationException;
import com.rohith.javavirtualos.kernel.metrics.KernelMetrics;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.pcb.ResourceInfo;
import com.rohith.javavirtualos.kernel.process.pcb.SchedulingInfo;
import com.rohith.javavirtualos.kernel.process.state.ProcessState;
import com.rohith.javavirtualos.kernel.process.state.ProcessStateMachine;
import com.rohith.javavirtualos.kernel.resource.ResourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessManager {
    private final Map<Integer, ProcessControlBlock> processTable = new ConcurrentHashMap<>();
    private final PIDGenerator pidGenerator;
    private final KernelEventBus eventBus;
    private final KernelMetrics metrics;
    private final KernelConfig config;
    private final ResourceManager resourceManager;

    public ProcessManager(PIDGenerator pidGenerator, KernelEventBus eventBus, KernelMetrics metrics, KernelConfig config, ResourceManager resourceManager) {
        this.pidGenerator = pidGenerator;
        this.eventBus = eventBus;
        this.metrics = metrics;
        this.config = config;
        this.resourceManager = resourceManager;
    }

    public ProcessControlBlock createProcess(String commandName, User owner, ProcessTask task, int parentPid) {
        if (processTable.size() >= config.getMaxProcesses()) {
            throw new ResourceAllocationException("Max processes limit reached.");
        }
        
        int pid = pidGenerator.generateId();
        SchedulingInfo sched = new SchedulingInfo(config.getDefaultPriority(), System.currentTimeMillis());
        ResourceInfo res = new ResourceInfo(0);
        
        ProcessControlBlock pcb = new ProcessControlBlock(pid, parentPid, commandName, owner, task, sched, res);
        
        if (parentPid > 0 && processTable.containsKey(parentPid)) {
            processTable.get(parentPid).addChild(pid);
        }
        
        processTable.put(pid, pcb);
        metrics.incrementProcessesCreated();
        eventBus.publish(new ProcessCreatedEvent(pid, commandName));
        
        return pcb;
    }

    public void startProcess(int pid) {
        ProcessControlBlock pcb = findByPID(pid);
        ProcessStateMachine.validateTransition(pcb.getState(), ProcessState.READY);
        changeState(pcb, ProcessState.READY);
        
        ProcessStateMachine.validateTransition(pcb.getState(), ProcessState.RUNNING);
        changeState(pcb, ProcessState.RUNNING);
        
        Thread t = new Thread(pcb.getTask(), "VP-" + pid + "-" + pcb.getCommandName());
        pcb.setThread(t);
        pcb.setStartTime(System.currentTimeMillis());
        metrics.incrementRunningProcesses();
        
        t.start();
        
        // Monitor for natural termination
        new Thread(() -> {
            try {
                t.join();
                if (pcb.getState() != ProcessState.TERMINATED) {
                    changeState(pcb, ProcessState.TERMINATED);
                    cleanupResources(pcb);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void pauseProcess(int pid, User requestor) {
        ProcessControlBlock pcb = findByPID(pid);
        checkPermission(pcb, requestor);
        ProcessStateMachine.validateTransition(pcb.getState(), ProcessState.SUSPENDED);
        
        pcb.getTask().pause();
        changeState(pcb, ProcessState.SUSPENDED);
    }

    public void resumeProcess(int pid, User requestor) {
        ProcessControlBlock pcb = findByPID(pid);
        checkPermission(pcb, requestor);
        ProcessStateMachine.validateTransition(pcb.getState(), ProcessState.READY);
        
        pcb.getTask().resume();
        changeState(pcb, ProcessState.READY);
        changeState(pcb, ProcessState.RUNNING);
    }

    public void terminateProcess(int pid, User requestor) {
        ProcessControlBlock pcb = findByPID(pid);
        checkPermission(pcb, requestor);
        
        pcb.getTask().stop();
        changeState(pcb, ProcessState.TERMINATED);
        cleanupResources(pcb);
    }
    
    private void cleanupResources(ProcessControlBlock pcb) {
        pcb.setEndTime(System.currentTimeMillis());
        resourceManager.deallocateMemory(pcb);
        metrics.decrementRunningProcesses();
    }

    public ProcessControlBlock findByPID(int pid) {
        ProcessControlBlock pcb = processTable.get(pid);
        if (pcb == null) {
            throw new ProcessNotFoundException("No process found with PID: " + pid);
        }
        return pcb;
    }

    public List<ProcessControlBlock> findByName(String name) {
        List<ProcessControlBlock> result = new ArrayList<>();
        for (ProcessControlBlock pcb : processTable.values()) {
            if (pcb.getCommandName().equals(name)) {
                result.add(pcb);
            }
        }
        return result;
    }

    public List<ProcessControlBlock> listProcesses() {
        return new ArrayList<>(processTable.values());
    }

    public void cleanupZombieProcesses() {
        processTable.entrySet().removeIf(entry -> entry.getValue().getState() == ProcessState.TERMINATED);
    }

    public void changePriority(int pid, int priority, User requestor) {
        ProcessControlBlock pcb = findByPID(pid);
        checkPermission(pcb, requestor);
        pcb.getSchedulingInfo().setPriority(priority);
    }

    private void changeState(ProcessControlBlock pcb, ProcessState newState) {
        ProcessState oldState = pcb.getState();
        pcb.setState(newState);
        eventBus.publish(new ProcessStateChangedEvent(pcb.getPid(), pcb.getCommandName(), pcb.getOwner().getUsername(), oldState.name(), newState.name()));
    }

    private void checkPermission(ProcessControlBlock pcb, User requestor) {
        if (!"root".equals(requestor.getUsername()) && !pcb.getOwner().getUsername().equals(requestor.getUsername())) {
            throw new PermissionDeniedException("Permission denied for process " + pcb.getPid());
        }
    }
}
