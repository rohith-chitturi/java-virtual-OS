package com.rohith.javavirtualos.kernel;

/**
 * Holds global OS metadata.
 */
public class SystemContext {
    private final String osName;
    private final String osVersion;
    private final String osBuild;
    private final String javaTarget;
    private final String osStatus;
    private final String hostname;
    private final long bootTime;

    public SystemContext(ConfigManager configManager) {
        this.osName = configManager.getProperty("os.name", "Java Virtual OS");
        this.osVersion = configManager.getProperty("os.version", "Unknown");
        this.osBuild = configManager.getProperty("os.build", "Unknown");
        this.javaTarget = configManager.getProperty("java.target", "21");
        this.osStatus = configManager.getProperty("os.status", "Development");
        this.hostname = configManager.getProperty("hostname", "javavm");
        this.bootTime = System.currentTimeMillis();
    }

    public String getOsName() { return osName; }
    public String getOsVersion() { return osVersion; }
    public String getOsBuild() { return osBuild; }
    public String getJavaTarget() { return javaTarget; }
    public String getOsStatus() { return osStatus; }
    public String getHostname() { return hostname; }
    public long getBootTime() { return bootTime; }
}
