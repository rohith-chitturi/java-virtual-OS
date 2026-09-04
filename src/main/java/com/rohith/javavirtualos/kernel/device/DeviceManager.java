package com.rohith.javavirtualos.kernel.device;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.DeviceEvent.*;
import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.filesystem.model.DeviceNode;
import com.rohith.javavirtualos.exceptions.FileSystemException;

import java.io.IOException;

public class DeviceManager {
    private final DeviceRegistry registry;
    private final DeviceStatistics statistics;
    private final KernelEventBus eventBus;
    private final FileSystemManager fileSystemManager;
    private final DeviceValidator validator;

    public DeviceManager(KernelEventBus eventBus, FileSystemManager fileSystemManager) {
        this.registry = new DeviceRegistry();
        this.statistics = new DeviceStatistics();
        this.eventBus = eventBus;
        this.fileSystemManager = fileSystemManager;
        this.validator = new DeviceValidator();
    }

    public void registerDevice(DeviceDriver driver) {
        try {
            validator.validateRegistration(driver, registry);
            driver.init();
            statistics.recordInit();
            registry.register(driver);
            eventBus.publish(new DeviceRegisteredEvent(driver.getDescriptor()));

            // Mount the device in VFS
            String mountPath = "/dev/" + driver.getDescriptor().getName();
            driver.getDescriptor().setMountPath(mountPath);
            DeviceNode deviceNode = new DeviceNode("root", driver, this);
            fileSystemManager.mountDevice(mountPath, deviceNode);
            eventBus.publish(new DeviceMountedEvent(driver.getDescriptor()));
            
        } catch (IOException | FileSystemException | IllegalArgumentException e) {
            statistics.recordError();
            if (driver != null && driver.getDescriptor() != null) {
                eventBus.publish(new DeviceFailureEvent(driver.getDescriptor(), "Init failed: " + e.getMessage()));
                driver.getDescriptor().setState(DeviceState.FAILED);
            }
        }
    }

    public void unregisterDevice(String name) {
        DeviceDriver driver = registry.getDriver(name);
        if (driver != null) {
            driver.shutdown();
            registry.unregister(name);
            eventBus.publish(new DeviceUnregisteredEvent(driver.getDescriptor()));
        }
    }

    public DeviceRegistry getRegistry() { return registry; }
    public DeviceStatistics getStatistics() { return statistics; }

    public void recordRead(DeviceDriver driver, int bytes) {
        statistics.recordRead(bytes);
        eventBus.publish(new DeviceReadEvent(driver.getDescriptor(), bytes));
    }

    public void recordWrite(DeviceDriver driver, int bytes) {
        statistics.recordWrite(bytes);
        eventBus.publish(new DeviceWriteEvent(driver.getDescriptor(), bytes));
    }

    public void recordError(DeviceDriver driver, String reason) {
        statistics.recordError();
        eventBus.publish(new DeviceFailureEvent(driver.getDescriptor(), reason));
    }
}
