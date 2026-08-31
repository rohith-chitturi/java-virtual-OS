package com.rohith.javavirtualos.kernel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

import com.rohith.javavirtualos.kernel.core.KernelConfig;
import com.rohith.javavirtualos.kernel.core.KernelTick;
import com.rohith.javavirtualos.kernel.core.MultiCoreProcessor;
import com.rohith.javavirtualos.kernel.device.DeviceManager;
import com.rohith.javavirtualos.kernel.device.drivers.NullDevice;
import com.rohith.javavirtualos.kernel.device.drivers.RandomDevice;
import com.rohith.javavirtualos.kernel.device.drivers.ZeroDevice;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.boot.FileSystemMountedEvent;
import com.rohith.javavirtualos.kernel.events.boot.KernelBootStartedEvent;
import com.rohith.javavirtualos.kernel.events.boot.KernelReadyEvent;
import com.rohith.javavirtualos.kernel.events.boot.MemoryInitializedEvent;
import com.rohith.javavirtualos.kernel.events.boot.NetworkInitializedEvent;
import com.rohith.javavirtualos.kernel.metrics.ExecutionTimeline;
import com.rohith.javavirtualos.kernel.metrics.KernelMetrics;
import com.rohith.javavirtualos.kernel.network.NetworkManager;
import com.rohith.javavirtualos.kernel.process.manager.ProcessManager;
import com.rohith.javavirtualos.kernel.core.PIDGenerator;
import com.rohith.javavirtualos.kernel.process.scheduler.KernelDispatcher;
import com.rohith.javavirtualos.kernel.scheduler.Scheduler;
import com.rohith.javavirtualos.kernel.scheduler.SchedulerStatistics;
import com.rohith.javavirtualos.kernel.scheduler.cfs.CompletelyFairScheduler;
import com.rohith.javavirtualos.kernel.resource.ResourceManager;
import com.rohith.javavirtualos.kernel.SecurityManager;
import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.services.DefaultFileSystemService;
import com.rohith.javavirtualos.services.DefaultProcessService;
import com.rohith.javavirtualos.shell.Shell;

import com.rohith.javavirtualos.kernel.process.runtime.syscall.SystemCallDispatcher;
import com.rohith.javavirtualos.kernel.process.runtime.syscall.SysWriteHandler;
import com.rohith.javavirtualos.kernel.process.runtime.syscall.SysReadHandler;
import com.rohith.javavirtualos.kernel.process.runtime.syscall.SysSleepHandler;
import com.rohith.javavirtualos.kernel.process.runtime.syscall.SysYieldHandler;
import com.rohith.javavirtualos.kernel.process.runtime.syscall.SysExitHandler;
import com.rohith.javavirtualos.kernel.process.runtime.syscall.SysGetPidHandler;
import com.rohith.javavirtualos.kernel.process.runtime.syscall.SysGetUidHandler;

/**
 * Responsible for the startup sequence of the virtual OS.
 */
public class BootLoader {

    public Kernel boot() {
        printBanner();
        System.out.println("Booting Java Virtual OS...\n");

        KernelEventBus eventBus = new KernelEventBus();
        eventBus.publish(new KernelBootStartedEvent());

        ConfigManager configManager = new ConfigManager();
        configManager.load("/system.properties");
        SystemContext systemContext = new SystemContext(configManager);

        KernelConfig config = new KernelConfig();

        System.out.println("[ OK ] Initializing Memory Manager");
        ResourceManager resourceManager = new ResourceManager(config.getMaxMemory());
        eventBus.publish(new MemoryInitializedEvent(config.getMaxMemory()));

        SecurityManager securityManager = new SecurityManager();
        UserManager userManager = new UserManager();

        System.out.println("[ OK ] Mounting Virtual File System");
        FileSystemManager fsManager = new FileSystemManager();
        fsManager.setSecurityManager(securityManager);
        FileSystemService fsService = new DefaultFileSystemService(fsManager);
        eventBus.publish(new FileSystemMountedEvent("/"));
        
        try {
            User rootUser = userManager.getUser("root");
            fsManager.createDirectory("/home", fsManager.getRoot(), rootUser);
            fsManager.createDirectory("/home/root", fsManager.resolveDirectory("/home", fsManager.getRoot()), rootUser);
            // Helper for creating files if missing
            java.util.function.BiConsumer<String, String> createDemoIfMissing = (name, code) -> {
                try {
                    String path = "/home/root/" + name;
                    if (fsManager.resolvePath(path, fsManager.getRoot()) == null) {
                        fsManager.createFile(name, fsManager.resolveDirectory("/home/root", fsManager.getRoot()), rootUser);
                        com.rohith.javavirtualos.filesystem.model.Inode inode = fsManager.resolvePath(path, fsManager.getRoot());
                        if (inode instanceof com.rohith.javavirtualos.filesystem.model.FileNode) {
                            ((com.rohith.javavirtualos.filesystem.model.FileNode) inode).setContent(code);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to create demo " + name + ": " + e.getMessage());
                }
            };

            createDemoIfMissing.accept("hello.vexe", 
                "LOAD R0 1\nLOAD R1 72\nSYSCALL 6 R0 R1\nLOAD R1 101\nSYSCALL 6 R0 R1\nLOAD R1 108\nSYSCALL 6 R0 R1\nSYSCALL 6 R0 R1\nLOAD R1 111\nSYSCALL 6 R0 R1\nLOAD R1 10\nSYSCALL 6 R0 R1\nEXIT 0\n");

            createDemoIfMissing.accept("calc.vexe", 
                "LOAD R0 15\nLOAD R1 27\nADD R0 R1\nSYSCALL 2 R0\nEXIT 0\n");

            createDemoIfMissing.accept("loop.vexe", 
                "LOAD R0 0\nLOAD R1 10\nCMP R0 R1\nJZ 7\nINC R0\nYIELD\nJMP 2\nEXIT 0\n");

            createDemoIfMissing.accept("scheduler_demo.vexe", 
                "LOAD R0 0\nLOAD R1 50\nCMP R0 R1\nJZ 7\nINC R0\nYIELD\nJMP 2\nEXIT 0\n");

            createDemoIfMissing.accept("memory_demo.vexe", 
                "LOAD R0 100\nLOAD R1 200\nADD R0 R1\nSYSCALL 2 R0\nEXIT 0\n");

            createDemoIfMissing.accept("filesystem_demo.vexe", 
                "SYSCALL 4 1 0 \"test.txt\"\nMOV R1 R0\nLOAD R2 65\nSYSCALL 6 R1 R2\nSYSCALL 10 R1\nEXIT 0\n");

            createDemoIfMissing.accept("pipe_demo.vexe", 
                "LOAD R0 0\nSYSCALL 5 R0\nMOV R2 R0\nLOAD R1 1\nSYSCALL 6 R1 R2\nEXIT 0\n");

        } catch (Exception e) {
            System.err.println("Failed to create demo files: " + e.getMessage());
        }

        NetworkManager networkManager = new NetworkManager(eventBus);
        eventBus.publish(new NetworkInitializedEvent());

        System.out.println("[ OK ] Initializing Device Manager");
        DeviceManager deviceManager = new DeviceManager(eventBus, fsManager);
        deviceManager.registerDevice(new NullDevice());
        deviceManager.registerDevice(new ZeroDevice());
        deviceManager.registerDevice(new RandomDevice());

        PIDGenerator pidGenerator = new PIDGenerator();
        KernelMetrics metrics = new KernelMetrics();
        ProcessManager processManager = new ProcessManager(pidGenerator, eventBus, metrics, config, resourceManager);
        DefaultProcessService processService = new DefaultProcessService(processManager);

        SystemCallDispatcher syscallDispatcher = new SystemCallDispatcher(processManager, fsManager);
        syscallDispatcher.registerHandler(SystemCallDispatcher.SYS_WRITE, new SysWriteHandler());
        syscallDispatcher.registerHandler(SystemCallDispatcher.SYS_PRINT, new SysWriteHandler()); // Reusing write handler for print
        syscallDispatcher.registerHandler(SystemCallDispatcher.SYS_READ, new SysReadHandler());
        syscallDispatcher.registerHandler(SystemCallDispatcher.SYS_SLEEP, new SysSleepHandler());
        syscallDispatcher.registerHandler(SystemCallDispatcher.SYS_YIELD, new SysYieldHandler());
        syscallDispatcher.registerHandler(SystemCallDispatcher.SYS_EXIT, new SysExitHandler());
        syscallDispatcher.registerHandler(SystemCallDispatcher.SYS_GETPID, new SysGetPidHandler());
        syscallDispatcher.registerHandler(SystemCallDispatcher.SYS_GETUID, new SysGetUidHandler());
        syscallDispatcher.registerHandler(SystemCallDispatcher.SYS_OPEN, new com.rohith.javavirtualos.kernel.process.runtime.syscall.SysOpenHandler(fsManager));
        syscallDispatcher.registerHandler(SystemCallDispatcher.SYS_CLOSE, new com.rohith.javavirtualos.kernel.process.runtime.syscall.SysCloseHandler());

        MultiCoreProcessor processor = new MultiCoreProcessor(4);
        List<Scheduler> schedulers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            schedulers.add(new CompletelyFairScheduler());
        }
        KernelDispatcher dispatcher = new KernelDispatcher(
            processor, schedulers, new KernelTick(), 
            eventBus, new ExecutionTimeline(), 
            2, new SchedulerStatistics(), syscallDispatcher);
        processService.setDispatcher(dispatcher);

        System.out.println("[ OK ] Starting Virtual Shell");
        com.rohith.javavirtualos.kernel.process.runtime.RuntimeStatistics runtimeStats = new com.rohith.javavirtualos.kernel.process.runtime.RuntimeStatistics();
        Shell shell = new Shell(systemContext, fsService, processService, userManager, networkManager, deviceManager, runtimeStats);

        Kernel kernel = new Kernel(
            systemContext, config, eventBus, metrics,
            resourceManager, processManager, networkManager,
            deviceManager, shell
        );

        System.out.println("\n---------------------------------------------------------");
        printSystemInfo(systemContext);
        System.out.println(" Type 'help' to view available commands.");
        System.out.println("---------------------------------------------------------\n");

        eventBus.publish(new KernelReadyEvent());

        return kernel;
    }

    private void printBanner() {
        try (InputStream is = getClass().getResourceAsStream("/banner.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("=========================================================");
            System.out.println("                 JAVA VIRTUAL OS");
            System.out.println("=========================================================\n");
        }
    }

    private void printSystemInfo(SystemContext context) {
        System.out.println(" " + context.getOsName());
        System.out.println(" Version : " + context.getOsVersion());
        System.out.println(" Build   : " + context.getOsBuild());
        System.out.println(" Java    : " + context.getJavaTarget());
        System.out.println(" Status  : " + context.getOsStatus());
    }
}

