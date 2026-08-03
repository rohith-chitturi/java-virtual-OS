package com.rohith.javavirtualos.kernel;

import com.rohith.javavirtualos.shell.Shell;
import com.rohith.javavirtualos.kernel.process.manager.ProcessManager;
import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.services.DefaultProcessService;
import com.rohith.javavirtualos.kernel.core.KernelConfig;
import com.rohith.javavirtualos.kernel.core.PIDGenerator;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.metrics.KernelMetrics;
import com.rohith.javavirtualos.kernel.resource.ResourceManager;
import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.services.DefaultFileSystemService;
import com.rohith.javavirtualos.kernel.device.DeviceManager;
import com.rohith.javavirtualos.kernel.device.drivers.NullDevice;
import com.rohith.javavirtualos.kernel.device.drivers.ZeroDevice;
import com.rohith.javavirtualos.kernel.device.drivers.RandomDevice;

/**
 * The central coordinator for the virtual operating system.
 */
public class Kernel {
    
    private ConfigManager configManager;
    private SystemContext systemContext;
    private Shell shell;
    private UserManager userManager;
    private SecurityManager securityManager;
    private ProcessManager processManager;
    private com.rohith.javavirtualos.kernel.network.NetworkManager networkManager;
    private DeviceManager deviceManager;
    private KernelConfig config;
    private KernelEventBus eventBus;
    private KernelMetrics metrics;
    private ResourceManager resourceManager;
    private PIDGenerator pidGenerator;

    public void initialize() {
        // 1. Initialize Configuration
        this.configManager = new ConfigManager();
        this.configManager.load("/system.properties");
        
        // 2. Initialize System Context
        this.systemContext = new SystemContext(configManager);
        
        System.out.println("[ OK ] Initializing Memory Manager"); // Stub output
        
        // File System Initialization
        System.out.println("[ OK ] Mounting Virtual File System");
        FileSystemManager fsManager = new FileSystemManager();
        FileSystemService fsService = new DefaultFileSystemService(fsManager);

        System.out.println("[ OK ] Loading Command Registry");
        System.out.println("[ OK ] Initializing Subsystems");
        
        this.config = new KernelConfig();
        this.eventBus = new KernelEventBus();
        this.metrics = new KernelMetrics();
        this.pidGenerator = new PIDGenerator();
        this.resourceManager = new ResourceManager(config.getMaxMemory());
        
        this.processManager = new ProcessManager(pidGenerator, eventBus, metrics, config, resourceManager);
        DefaultProcessService processService = new DefaultProcessService(processManager);
        
        // Initialize Simulator Dispatcher (Multi-core, CFS by default)
        com.rohith.javavirtualos.kernel.core.Processor processor = new com.rohith.javavirtualos.kernel.core.MultiCoreProcessor(4);
        java.util.List<com.rohith.javavirtualos.kernel.scheduler.Scheduler> schedulers = new java.util.ArrayList<>();
        for (int i = 0; i < 4; i++) {
            schedulers.add(new com.rohith.javavirtualos.kernel.scheduler.cfs.CompletelyFairScheduler());
        }
        com.rohith.javavirtualos.kernel.process.scheduler.KernelDispatcher dispatcher = 
            new com.rohith.javavirtualos.kernel.process.scheduler.KernelDispatcher(
                processor, schedulers, new com.rohith.javavirtualos.kernel.core.KernelTick(), 
                eventBus, new com.rohith.javavirtualos.kernel.metrics.ExecutionTimeline(), 
                2, new com.rohith.javavirtualos.kernel.scheduler.SchedulerStatistics());
        processService.setDispatcher(dispatcher);

        System.out.println("[ OK ] Initializing Security Manager");
        this.securityManager = new SecurityManager();
        this.userManager = new UserManager();
        fsManager.setSecurityManager(securityManager); // We'll add this method
        
        this.networkManager = new com.rohith.javavirtualos.kernel.network.NetworkManager(eventBus);
        
        System.out.println("[ OK ] Initializing Device Manager");
        this.deviceManager = new DeviceManager(eventBus, fsManager);
        this.deviceManager.registerDevice(new NullDevice());
        this.deviceManager.registerDevice(new ZeroDevice());
        this.deviceManager.registerDevice(new RandomDevice());
        
        // 3. Initialize Shell
        System.out.println("[ OK ] Starting Virtual Shell");
        this.shell = new Shell(systemContext, fsService, processService, userManager, networkManager, deviceManager);
    }

    public void startShell() {
        if (shell != null) {
            shell.start();
        } else {
            System.err.println("PANIC: Shell is not initialized!");
            System.exit(1);
        }
    }

    public SystemContext getSystemContext() {
        return systemContext;
    }
}
