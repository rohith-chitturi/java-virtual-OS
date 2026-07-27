package com.rohith.javavirtualos.kernel.device;

public class DeviceDescriptor {
    private final String name;
    private final DeviceType type;
    private final String driverVersion;
    private DeviceState state;
    private String mountPath;

    public DeviceDescriptor(String name, DeviceType type, String driverVersion) {
        this.name = name;
        this.type = type;
        this.driverVersion = driverVersion;
        this.state = DeviceState.INITIALIZED;
    }

    public String getName() { return name; }
    public DeviceType getType() { return type; }
    public String getDriverVersion() { return driverVersion; }
    public DeviceState getState() { return state; }
    public void setState(DeviceState state) { this.state = state; }
    public String getMountPath() { return mountPath; }
    public void setMountPath(String mountPath) { this.mountPath = mountPath; }
}
