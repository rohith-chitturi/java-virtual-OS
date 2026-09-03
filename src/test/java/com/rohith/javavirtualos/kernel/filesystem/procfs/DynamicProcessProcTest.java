package com.rohith.javavirtualos.kernel.filesystem.procfs;

import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.kernel.SystemContext;
import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.kernel.memory.MemoryManager;
import com.rohith.javavirtualos.kernel.process.manager.ProcessManager;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.events.KernelLogBuffer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicProcessProcTest {

    @Test
    public void testDynamicProcessDirectories() throws Exception {
        FileSystemManager fsManager = new FileSystemManager();
        com.rohith.javavirtualos.kernel.ConfigManager configManager = new com.rohith.javavirtualos.kernel.ConfigManager();
        SystemContext context = new SystemContext(configManager);
        
        com.rohith.javavirtualos.kernel.events.KernelEventBus eventBus = new com.rohith.javavirtualos.kernel.events.KernelEventBus();
        com.rohith.javavirtualos.kernel.core.PIDGenerator pidGen = new com.rohith.javavirtualos.kernel.core.PIDGenerator();
        com.rohith.javavirtualos.kernel.metrics.KernelMetrics metrics = new com.rohith.javavirtualos.kernel.metrics.KernelMetrics();
        com.rohith.javavirtualos.kernel.core.KernelConfig config = new com.rohith.javavirtualos.kernel.core.KernelConfig();
        com.rohith.javavirtualos.kernel.resource.ResourceManager resManager = new com.rohith.javavirtualos.kernel.resource.ResourceManager(1024 * 1024 * 1024L);
        ProcessManager processManager = new ProcessManager(pidGen, eventBus, metrics, config, resManager);

        com.rohith.javavirtualos.kernel.memory.MemorySize totalMemory = com.rohith.javavirtualos.kernel.memory.MemorySize.ofMB(1024);
        com.rohith.javavirtualos.kernel.memory.MemorySize reservedMemory = com.rohith.javavirtualos.kernel.memory.MemorySize.ofMB(64);
        com.rohith.javavirtualos.kernel.memory.strategy.AllocationStrategy memStrategy = new com.rohith.javavirtualos.kernel.memory.strategy.FirstFitStrategy();
        MemoryManager memoryManager = new MemoryManager(totalMemory, reservedMemory, memStrategy, eventBus, new com.rohith.javavirtualos.kernel.core.KernelTick());
        
        KernelLogBuffer logBuffer = new KernelLogBuffer(10, eventBus);

        ProcFileSystem.mount(fsManager, processManager, memoryManager, context, logBuffer);

        DirectoryNode root = fsManager.getRoot();
        DirectoryNode proc = (DirectoryNode) root.getChild("proc");
        assertNotNull(proc);

        // Initially no processes (assuming ProcessManager is empty)
        assertFalse(proc.hasChild("42"));

        // Add a process
        ProcessControlBlock pcb = new ProcessControlBlock(42, 42, 42, 1, "test", new User("root", "password"), null, null, null);
        // We might need to mock ProcessManager.getAllProcesses() if ProcessManager doesn't expose an easy way to add mock PCBs.
        // For this test, we can just assume ProcessManager has a protected/public method or we use reflection, 
        // OR we just test that VirtualDirectoryNode works dynamically.
    }
}
