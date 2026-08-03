package com.rohith.javavirtualos.kernel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import com.rohith.javavirtualos.kernel.core.*;
import com.rohith.javavirtualos.kernel.device.*;
import com.rohith.javavirtualos.kernel.device.drivers.*;
import com.rohith.javavirtualos.kernel.events.*;
import com.rohith.javavirtualos.kernel.events.boot.*;
import com.rohith.javavirtualos.kernel.metrics.*;
import com.rohith.javavirtualos.kernel.network.*;
import com.rohith.javavirtualos.kernel.process.manager.*;
import com.rohith.javavirtualos.kernel.process.scheduler.*;
import com.rohith.javavirtualos.kernel.scheduler.cfs.*;
import com.rohith.javavirtualos.kernel.scheduler.*;
import com.rohith.javavirtualos.kernel.resource.*;
import com.rohith.javavirtualos.filesystem.*;
import com.rohith.javavirtualos.services.*;
import com.rohith.javavirtualos.shell.*;
import java.util.ArrayList;
import java.util.List;

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

        MultiCoreProcessor processor = new MultiCoreProcessor(4);
        List<Scheduler> schedulers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            schedulers.add(new CompletelyFairScheduler());
        }
        KernelDispatcher dispatcher = new KernelDispatcher(
            processor, schedulers, new KernelTick(), 
            eventBus, new ExecutionTimeline(), 
            2, new SchedulerStatistics());
        processService.setDispatcher(dispatcher);

        System.out.println("[ OK ] Starting Virtual Shell");
        Shell shell = new Shell(systemContext, fsService, processService, userManager, networkManager, deviceManager);

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
