package com.rohith.javavirtualos.kernel;

import com.rohith.javavirtualos.shell.Shell;
import com.rohith.javavirtualos.kernel.process.manager.ProcessManager;
import com.rohith.javavirtualos.kernel.core.KernelConfig;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.metrics.KernelMetrics;
import com.rohith.javavirtualos.kernel.resource.ResourceManager;
import com.rohith.javavirtualos.kernel.device.DeviceManager;


/**
 * The central coordinator for the virtual operating system.
 */
public class Kernel {
    
    private final SystemContext systemContext;
    private final KernelConfig config;
    private final KernelEventBus eventBus;
    private final KernelMetrics metrics;
    private final ResourceManager resourceManager;
    private final ProcessManager processManager;
    private final com.rohith.javavirtualos.kernel.network.NetworkManager networkManager;
    private final DeviceManager deviceManager;
    private final Shell shell;

    public Kernel(SystemContext systemContext, KernelConfig config, KernelEventBus eventBus,
                  KernelMetrics metrics, ResourceManager resourceManager, ProcessManager processManager,
                  com.rohith.javavirtualos.kernel.network.NetworkManager networkManager, DeviceManager deviceManager, Shell shell) {
        this.systemContext = systemContext;
        this.config = config;
        this.eventBus = eventBus;
        this.metrics = metrics;
        this.resourceManager = resourceManager;
        this.processManager = processManager;
        this.networkManager = networkManager;
        this.deviceManager = deviceManager;
        this.shell = shell;
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

    public KernelConfig getConfig() {
        return config;
    }

    public KernelEventBus getEventBus() {
        return eventBus;
    }

    public KernelMetrics getMetrics() {
        return metrics;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public com.rohith.javavirtualos.kernel.network.NetworkManager getNetworkManager() {
        return networkManager;
    }

    public DeviceManager getDeviceManager() {
        return deviceManager;
    }
}
