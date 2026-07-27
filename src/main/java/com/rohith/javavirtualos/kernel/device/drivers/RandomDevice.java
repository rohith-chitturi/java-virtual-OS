package com.rohith.javavirtualos.kernel.device.drivers;

import com.rohith.javavirtualos.kernel.device.DeviceDescriptor;
import com.rohith.javavirtualos.kernel.device.DeviceDriver;
import com.rohith.javavirtualos.kernel.device.DeviceState;
import com.rohith.javavirtualos.kernel.device.DeviceType;
import com.rohith.javavirtualos.kernel.device.DeviceCapability;
import java.util.EnumSet;

import java.security.SecureRandom;

public class RandomDevice implements DeviceDriver {
    private final DeviceDescriptor descriptor;
    private final SecureRandom random;

    public RandomDevice() {
        this.descriptor = new DeviceDescriptor(
            "random", DeviceType.CHARACTER, "1.0", "JavaVirtualOS", "Secure random byte generator",
            EnumSet.of(DeviceCapability.READ)
        );
        this.random = new SecureRandom();
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
        byte[] bytes = new byte[maxBytes];
        random.nextBytes(bytes);
        return bytes;
    }

    @Override
    public int write(byte[] data) {
        // According to Linux, writing to /dev/random adds entropy, but we'll just discard it for now.
        return data.length; 
    }

    @Override
    public boolean healthCheck() {
        return true;
    }
}
