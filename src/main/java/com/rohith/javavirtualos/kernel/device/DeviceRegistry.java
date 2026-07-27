package com.rohith.javavirtualos.kernel.device;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceRegistry {
    private final Map<String, DeviceDriver> registry = new ConcurrentHashMap<>();

    public void register(DeviceDriver driver) {
        if (driver == null || driver.getDescriptor() == null) {
            throw new IllegalArgumentException("Driver or descriptor cannot be null");
        }
        registry.put(driver.getDescriptor().getName(), driver);
    }

    public void unregister(String deviceName) {
        registry.remove(deviceName);
    }

    public DeviceDriver getDriver(String deviceName) {
        return registry.get(deviceName);
    }

    public Collection<DeviceDriver> getAllDrivers() {
        return registry.values();
    }
}
