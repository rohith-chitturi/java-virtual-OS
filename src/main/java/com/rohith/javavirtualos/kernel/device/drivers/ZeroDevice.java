package com.rohith.javavirtualos.kernel.device.drivers;

import com.rohith.javavirtualos.kernel.device.DeviceDescriptor;
import com.rohith.javavirtualos.kernel.device.DeviceDriver;
import com.rohith.javavirtualos.kernel.device.DeviceState;
import com.rohith.javavirtualos.kernel.device.DeviceType;

public class ZeroDevice implements DeviceDriver {
    private final DeviceDescriptor descriptor;

    public ZeroDevice() {
        this.descriptor = new DeviceDescriptor("zero", DeviceType.CHARACTER, "1.0");
    }

    @Override
    public DeviceDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void init() {
        descriptor.setState(DeviceState.ONLINE);
    }

    @Override
    public void shutdown() {
        descriptor.setState(DeviceState.OFFLINE);
    }

    @Override
    public byte[] read(int maxBytes) {
        return new byte[maxBytes]; // Returns a byte array initialized to zeroes
    }

    @Override
    public int write(byte[] data) {
        return data.length; // Discard and say it was all written
    }
}
