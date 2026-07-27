package com.rohith.javavirtualos.kernel.network;

public class NetworkInterface {
    private final String name;
    private final IPAddress ipAddress;
    private final String macAddress;
    private boolean isUp;

    public NetworkInterface(String name, IPAddress ipAddress, String macAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.isUp = false;
    }

    public String getName() { return name; }
    public IPAddress getIpAddress() { return ipAddress; }
    public String getMacAddress() { return macAddress; }
    public boolean isUp() { return isUp; }

    public void setUp(boolean up) {
        this.isUp = up;
    }

    @Override
    public String toString() {
        return name + " (" + ipAddress + ")";
    }
}
