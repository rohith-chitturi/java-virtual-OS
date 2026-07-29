package com.rohith.javavirtualos.kernel.process.manager;

import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.kernel.core.KernelConfig;
import com.rohith.javavirtualos.kernel.core.PIDGenerator;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.metrics.KernelMetrics;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.state.ProcessState;
import com.rohith.javavirtualos.kernel.resource.ResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProcessManagerTest {
    private ProcessManager processManager;
    private User root;

    @BeforeEach
    void setUp() {
        PIDGenerator pidGen = new PIDGenerator();
        KernelEventBus bus = new KernelEventBus();
        KernelMetrics metrics = new KernelMetrics();
        KernelConfig config = new KernelConfig();
        ResourceManager resourceManager = new ResourceManager(config.getMaxMemory());
        processManager = new ProcessManager(pidGen, bus, metrics, config, resourceManager);
        root = new User("root", "password");
    }

    @Test
    void testProcessCreationAndPriority() {
        ProcessTask task = new ProcessTask(() -> {});
        ProcessControlBlock pcb = processManager.createProcess("test1", root, task, 1);
        assertEquals(ProcessState.NEW, pcb.getState());
        assertEquals("test1", pcb.getCommandName());
        
        processManager.changePriority(pcb.getPid(), 10, root);
        assertEquals(10, pcb.getSchedulingInfo().getPriority());
    }

    @Test
    void testPauseAndResume() {
        ProcessTask task = new ProcessTask(() -> {
            try { Thread.sleep(500); } catch(Exception ignored) {}
        });
        ProcessControlBlock pcb = processManager.createProcess("test2", root, task, 1);
        processManager.startProcess(pcb.getPid());
        
        try { Thread.sleep(50); } catch(Exception ignored) {}
        
        processManager.pauseProcess(pcb.getPid(), root);
        assertEquals(ProcessState.SUSPENDED, pcb.getState());
        
        processManager.resumeProcess(pcb.getPid(), root);
        assertEquals(ProcessState.RUNNING, pcb.getState());
        processManager.terminateProcess(pcb.getPid(), root);
    }
    @Test
    void testZombieCleanup() {
        ProcessTask task = new ProcessTask(() -> {});
        // Create init (PID 1) so zombie gets PID 2
        ProcessControlBlock init = processManager.createProcess("init", root, task, 0);
        ProcessControlBlock pcb = processManager.createProcess("zombie", root, task, 1);
        processManager.terminateProcess(pcb.getPid(), root);
        assertEquals(ProcessState.ZOMBIE, pcb.getState());
        
        processManager.waitProcess(init.getPid(), pcb.getPid()); // Wait on the child
        assertEquals(ProcessState.TERMINATED, pcb.getState());
        
        processManager.cleanupZombieProcesses();
        assertThrows(Exception.class, () -> processManager.findByPID(pcb.getPid()));
    }

    @Test
    void testThreadCreationAndSharedResources() {
        ProcessTask task = new ProcessTask(() -> {});
        ProcessControlBlock parent = processManager.createProcess("parent", root, task, 1);
        
        ThreadAttributes attrs = new ThreadAttributes();
        ProcessControlBlock thread = processManager.createThread(parent.getPid(), task, attrs);
        
        assertEquals(parent.getPid(), thread.getTgid());
        assertNotEquals(parent.getPid(), thread.getPid());
        
        // Assert shared resources
        assertSame(parent.getResourceInfo(), thread.getResourceInfo());
        
        // Assert independent state
        parent.setState(ProcessState.RUNNING);
        thread.setState(ProcessState.READY);
        assertEquals(ProcessState.RUNNING, parent.getState());
        assertEquals(ProcessState.READY, thread.getState());
    }

    @Test
    void testProcessGroups() {
        ProcessTask task = new ProcessTask(() -> {});
        ProcessControlBlock p1 = processManager.createProcess("p1", root, task, 1);
        ProcessControlBlock p2 = processManager.createProcess("p2", root, task, 1);
        
        processManager.setpgid(p1.getPid(), 100);
        processManager.setpgid(p2.getPid(), 100);
        
        List<ProcessControlBlock> group = processManager.findByPgid(100);
        assertEquals(2, group.size());
        assertTrue(group.contains(p1));
        assertTrue(group.contains(p2));
    }

    @Test
    void testNestedOrphanReparenting() {
        // init is implicitly 1
        ProcessTask task = new ProcessTask(() -> {});
        
        // Create dummy init
        ProcessControlBlock init = processManager.createProcess("init", root, task, 0); 
        // We override its pid for the test
        try {
            java.lang.reflect.Field pidField = ProcessControlBlock.class.getDeclaredField("pid");
            pidField.setAccessible(true);
            pidField.set(init, 1);
        } catch (Exception e) {}
        
        ProcessControlBlock a = processManager.createProcess("A", root, task, 1);
        ProcessControlBlock b = processManager.createProcess("B", root, task, a.getPid());
        ProcessControlBlock c = processManager.createProcess("C", root, task, b.getPid());
        
        assertEquals(a.getPid(), b.getParentPid());
        assertEquals(b.getPid(), c.getParentPid());
        
        // Terminate A. B should reparent to 1. C remains child of B.
        processManager.terminateProcess(a.getPid(), root);
        
        assertEquals(1, b.getParentPid());
        assertEquals(b.getPid(), c.getParentPid());
        
        assertTrue(init.getChildrenPids().contains(b.getPid()));
    }

    @Test
    void test1000Processes() {
        ProcessTask task = new ProcessTask(() -> {});
        // Create init (PID 1) so bulk processes start from 2
        ProcessControlBlock init = processManager.createProcess("init", root, task, 0);
        
        List<ProcessControlBlock> pcbs = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            pcbs.add(processManager.createProcess("bulk-" + i, root, task, init.getPid()));
        }
        
        assertEquals(1001, processManager.listProcesses().size());
        
        for (ProcessControlBlock pcb : pcbs) {
            processManager.terminateProcess(pcb.getPid(), root);
        }
        
        processManager.cleanupZombieProcesses();
        assertEquals(1, processManager.listProcesses().size());
    }
}
