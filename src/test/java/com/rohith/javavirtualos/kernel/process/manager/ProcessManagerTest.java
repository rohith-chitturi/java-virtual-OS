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
        ProcessControlBlock pcb = processManager.createProcess("zombie", root, task, 1);
        processManager.terminateProcess(pcb.getPid(), root);
        assertEquals(ProcessState.TERMINATED, pcb.getState());
        
        processManager.cleanupZombieProcesses();
        assertThrows(Exception.class, () -> processManager.findByPID(pcb.getPid()));
    }

    @Test
    void test1000Processes() {
        List<ProcessControlBlock> pcbs = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ProcessTask task = new ProcessTask(() -> {});
            pcbs.add(processManager.createProcess("bulk-" + i, root, task, 1));
        }
        
        assertEquals(1000, processManager.listProcesses().size());
        
        for (ProcessControlBlock pcb : pcbs) {
            processManager.terminateProcess(pcb.getPid(), root);
        }
        
        processManager.cleanupZombieProcesses();
        assertEquals(0, processManager.listProcesses().size());
    }
}
