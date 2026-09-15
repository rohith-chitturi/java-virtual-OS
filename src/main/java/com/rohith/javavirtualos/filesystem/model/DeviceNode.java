package com.rohith.javavirtualos.filesystem.model;

import com.rohith.javavirtualos.kernel.device.DeviceDriver;
import com.rohith.javavirtualos.kernel.device.DeviceManager;
import com.rohith.javavirtualos.kernel.device.DeviceState;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DeviceNode extends Inode {
    private final DeviceDriver driver;
    private final DeviceManager deviceManager;

    public DeviceNode(String owner, DeviceDriver driver, DeviceManager deviceManager) {
        super(owner);
        this.driver = driver;
        this.deviceManager = deviceManager;
    }

    private boolean checkHealth() {
        if (driver.getDescriptor().getState() == DeviceState.FAILED) {
            deviceManager.recordError(driver, "Device is in FAILED state");
            return false;
        }
        if (!driver.healthCheck()) {
            deviceManager.recordError(driver, "Health check failed");
            driver.getDescriptor().setState(DeviceState.FAILED);
            return false;
        }
        return true;
    }

    @Override
    public FileType getType() {
        return FileType.DEVICE;
    }

    public byte[] readDeviceBytes() {
        if (!checkHealth()) return new byte[0];
        try {
            byte[] data = driver.read(4096);
            deviceManager.recordRead(driver, data.length);
            return data;
        } catch (IOException e) {
            deviceManager.recordError(driver, e.getMessage());
            return new byte[0];
        }
    }

    public int writeDeviceBytes(byte[] bytes) {
        if (!checkHealth()) return 0;
        try {
            int written = driver.write(bytes);
            deviceManager.recordWrite(driver, written);
            return written;
        } catch (IOException e) {
            deviceManager.recordError(driver, e.getMessage());
            return 0;
        }
    }
    
    @Override
    public long calculateSize() {
        return 0; // Devices conceptually have 0 size in standard VFS tools until read
    }

    public DeviceDriver getDriver() {
        return driver;
    }
}
