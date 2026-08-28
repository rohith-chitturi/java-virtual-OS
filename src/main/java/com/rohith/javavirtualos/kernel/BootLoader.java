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
            
            // hello.vexe
            fsManager.createFile("/home/root/hello.vexe", fsManager.getRoot(), rootUser);
            com.rohith.javavirtualos.filesystem.model.Inode hello = fsManager.resolvePath("/home/root/hello.vexe", fsManager.getRoot());
            if (hello instanceof com.rohith.javavirtualos.filesystem.model.FileNode) {
                String code = "LOAD R0 42\nSYSCALL 1 1 R0\nEXIT 0\n";
                ((com.rohith.javavirtualos.filesystem.model.FileNode) hello).setContent(code);
            }
            
            // calc.vexe
            fsManager.createFile("/home/root/calc.vexe", fsManager.getRoot(), rootUser);
            com.rohith.javavirtualos.filesystem.model.Inode calc = fsManager.resolvePath("/home/root/calc.vexe", fsManager.getRoot());
            if (calc instanceof com.rohith.javavirtualos.filesystem.model.FileNode) {
                String code = "LOAD R0 15\nLOAD R1 27\nADD R0 R1\nSYSCALL 1 1 R0\nEXIT 0\n";
                ((com.rohith.javavirtualos.filesystem.model.FileNode) calc).setContent(code);
            }
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

