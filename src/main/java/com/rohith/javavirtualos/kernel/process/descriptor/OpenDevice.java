package com.rohith.javavirtualos.kernel.process.descriptor;

import com.rohith.javavirtualos.filesystem.model.DeviceNode;

public class OpenDevice implements Descriptor {
    private final DeviceNode deviceNode;
    private int fd;
    private boolean open = true;

    public OpenDevice(DeviceNode deviceNode) {
        this.deviceNode = deviceNode;
    }

    @Override
    public int getFd() {
        return fd;
    }

    @Override
    public void setFd(int fd) {
        this.fd = fd;
    }

    @Override
    public void close() {
        open = false;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    public DeviceNode getDeviceNode() {
        return deviceNode;
    }
}
