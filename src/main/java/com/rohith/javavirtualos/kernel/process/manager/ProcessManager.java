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
import com.rohith.javavirtualos.kernel.process.pcb.ExitStatus;
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
        int pgid = pid;
        if (parentPid > 0 && processTable.containsKey(parentPid)) {
            pgid = processTable.get(parentPid).getPgid();
        }

        SchedulingInfo sched = new SchedulingInfo(config.getDefaultPriority(), System.currentTimeMillis());
        ResourceInfo res = new ResourceInfo(0);
        
        ProcessControlBlock pcb = new ProcessControlBlock(pid, pid, pgid, parentPid, commandName, owner, task, sched, res);
        
        if (parentPid > 0 && processTable.containsKey(parentPid)) {
            processTable.get(parentPid).addChild(pid);
        }
        
        processTable.put(pid, pcb);
        metrics.incrementProcessesCreated();
        eventBus.publish(new ProcessCreatedEvent(pid, commandName));
        
        return pcb;
    }

    public ProcessControlBlock createThread(int parentPid, ProcessTask task, ThreadAttributes attrs) {
        ProcessControlBlock parent = findByPID(parentPid);
        
        if (processTable.size() >= config.getMaxProcesses()) {
            throw new ResourceAllocationException("Max processes limit reached.");
        }
        
        int pid = pidGenerator.generateId();
        SchedulingInfo sched = new SchedulingInfo(attrs.getPriority(), System.currentTimeMillis());
        
        // Share ResourceInfo with parent
        ProcessControlBlock pcb = new ProcessControlBlock(pid, parent.getTgid(), parent.getPgid(), parentPid, 
                parent.getCommandName() + "-thread", parent.getOwner(), task, sched, parent.getResourceInfo());
        
        parent.addChild(pid);
        processTable.put(pid, pcb);
        metrics.incrementProcessesCreated();
        eventBus.publish(new ProcessCreatedEvent(pid, pcb.getCommandName()));
        
        return pcb;
    }

    public void startProcess(int pid) {
        ProcessControlBlock pcb = findByPID(pid);
        ProcessStateMachine.validateTransition(pcb.getState(), ProcessState.READY);
        changeState(pcb, ProcessState.READY);
        
        if (pcb.getVirtualMachine() != null) {
            // For user-space executables, the KernelDispatcher manages execution ticks directly.
            // We just set it to RUNNING (or leave it READY) and it will be picked up by the dispatcher.
            // Actually, we leave it READY so the KernelDispatcher's load balancer or queue picks it up.
            return; 
        }
        
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
                if (pcb.getState() != ProcessState.TERMINATED && pcb.getState() != ProcessState.ZOMBIE) {
                    pcb.setExitStatus(ExitStatus.normal(0));
                    changeState(pcb, ProcessState.ZOMBIE);
                    cleanupResources(pcb);
                    reparentOrphans(pcb);
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
        pcb.setExitStatus(ExitStatus.signaled(9)); // Default to SIGKILL for terminate
        changeState(pcb, ProcessState.ZOMBIE);
        cleanupResources(pcb);
        reparentOrphans(pcb);
    }
    
    private void reparentOrphans(ProcessControlBlock parent) {
        if (parent.getPid() == 1) {
            // If init dies, terminate all its children
            for (int childPid : new ArrayList<>(parent.getChildrenPids())) {
                ProcessControlBlock child = processTable.get(childPid);
                if (child != null) {
                    changeState(child, ProcessState.TERMINATED);
                    processTable.remove(childPid);
                }
            }
            parent.getChildrenPids().clear();
            return;
        }
        
        for (int childPid : new ArrayList<>(parent.getChildrenPids())) {
            ProcessControlBlock child = processTable.get(childPid);
            if (child != null) {
                child.setParentPid(1); // Reparent to init (PID 1)
                
                // If init (1) gets a zombie, reap it immediately
                if (child.getState() == ProcessState.ZOMBIE) {
                    changeState(child, ProcessState.TERMINATED);
                    processTable.remove(childPid);
                } else {
                    ProcessControlBlock init = processTable.get(1);
                    if (init != null) {
                        init.addChild(childPid);
                    }
                }
            }
        }
        parent.getChildrenPids().clear();
    }
    
    public ExitStatus waitProcess(int parentPid) {
        return waitProcess(parentPid, -1);
    }
    
    public ExitStatus waitProcess(int parentPid, int childPid) {
        ProcessControlBlock parent = findByPID(parentPid);
        
        for (int cPid : parent.getChildrenPids()) {
            if (childPid == -1 || cPid == childPid) {
                ProcessControlBlock child = processTable.get(cPid);
                if (child != null && child.getState() == ProcessState.ZOMBIE) {
                    ExitStatus status = child.getExitStatus();
                    changeState(child, ProcessState.TERMINATED);
                    processTable.remove(cPid);
                    parent.removeChild(cPid);
                    return status;
                }
            }
        }
        throw new IllegalStateException("No zombie child process found to wait on.");
    }
    
    private void cleanupResources(ProcessControlBlock pcb) {
        pcb.setEndTime(System.currentTimeMillis());
        // Close any remaining open file descriptors to release openReferenceCounts
        for (Integer fd : new java.util.ArrayList<>(pcb.getFileDescriptorTable().getAll().keySet())) {
            pcb.getFileDescriptorTable().close(fd);
        }
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
        // ZOMBIEs should only be cleaned by waitProcess(), but this handles force cleanup
        processTable.entrySet().removeIf(entry -> entry.getValue().getState() == ProcessState.TERMINATED || entry.getValue().getState() == ProcessState.ZOMBIE);
    }

    public void setpgid(int pid, int pgid) {
        ProcessControlBlock pcb = findByPID(pid);
        pcb.setPgid(pgid);
    }

    public List<ProcessControlBlock> findByPgid(int pgid) {
        List<ProcessControlBlock> result = new ArrayList<>();
        for (ProcessControlBlock pcb : processTable.values()) {
            if (pcb.getPgid() == pgid) {
                result.add(pcb);
            }
        }
        return result;
    }

    public void changePriority(int pid, int priority, User requestor) {
        ProcessControlBlock pcb = findByPID(pid);
        checkPermission(pcb, requestor);
        pcb.getSchedulingInfo().setPriority(priority);
    }

    private void changeState(ProcessControlBlock pcb, ProcessState newState) {
        ProcessState oldState = pcb.getState();
        pcb.setState(newState);
        eventBus.publish(new ProcessStateChangedEvent(pcb.getPid(), pcb.getOwner().getUsername(), oldState.name(), newState.name()));
    }

    private void checkPermission(ProcessControlBlock pcb, User requestor) {
        if (!"root".equals(requestor.getUsername()) && !pcb.getOwner().getUsername().equals(requestor.getUsername())) {
            throw new PermissionDeniedException("Permission denied for process " + pcb.getPid());
        }
    }
}
