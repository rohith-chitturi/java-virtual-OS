package com.rohith.javavirtualos.filesystem.model;

import com.rohith.javavirtualos.kernel.device.DeviceDriver;
import com.rohith.javavirtualos.kernel.device.DeviceManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DeviceNode extends FileNode {
    private final DeviceDriver driver;
    private final DeviceManager deviceManager;

    public DeviceNode(DeviceDriver driver, DeviceManager deviceManager, DirectoryNode parent) {
        super(driver.getDescriptor().getName(), "root", parent);
        this.driver = driver;
        this.deviceManager = deviceManager;
    }

    @Override
    public String getContent() {
        try {
            byte[] data = driver.read(4096);
            deviceManager.recordRead(driver, data.length);
            return new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            deviceManager.recordError(driver, e.getMessage());
            return "";
        }
    }

    @Override
    public void setContent(String content) {
        try {
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            int written = driver.write(bytes);
            deviceManager.recordWrite(driver, written);
        } catch (IOException e) {
            deviceManager.recordError(driver, e.getMessage());
        }
    }
    
    @Override
    public void appendContent(String additional) {
        setContent(additional);
    }
    
    @Override
    public long calculateSize() {
        return 0; // Devices conceptually have 0 size in standard VFS tools until read
    }

    public DeviceDriver getDriver() {
        return driver;
    }
}
