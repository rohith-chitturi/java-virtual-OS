package com.rohith.javavirtualos.kernel.device;

import java.util.EnumSet;
import java.util.Set;

public class DeviceDescriptor {
    private final String name;
    private final DeviceType type;
    private final String driverVersion;
    private final String vendor;
    private final String description;
    private final Set<DeviceCapability> capabilities;
    private DeviceState state;
    private String mountPath;

    public DeviceDescriptor(String name, DeviceType type, String driverVersion, String vendor, String description, Set<DeviceCapability> capabilities) {
        this.name = name;
        this.type = type;
        this.driverVersion = driverVersion;
        this.vendor = vendor != null ? vendor : "Unknown";
        this.description = description != null ? description : "";
        this.capabilities = capabilities != null && !capabilities.isEmpty() ? EnumSet.copyOf(capabilities) : EnumSet.noneOf(DeviceCapability.class);
        this.state = DeviceState.INITIALIZED;
    }

    public String getName() { return name; }
    public DeviceType getType() { return type; }
    public String getDriverVersion() { return driverVersion; }
    public String getVendor() { return vendor; }
    public String getDescription() { return description; }
    public Set<DeviceCapability> getCapabilities() { return EnumSet.copyOf(capabilities); }
    public DeviceState getState() { return state; }
    public void setState(DeviceState state) { this.state = state; }
    public String getMountPath() { return mountPath; }
    public void setMountPath(String mountPath) { this.mountPath = mountPath; }
}
