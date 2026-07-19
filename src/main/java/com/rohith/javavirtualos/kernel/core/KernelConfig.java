package com.rohith.javavirtualos.kernel.core;

import java.io.InputStream;
import java.util.Properties;

public class KernelConfig {
    private final Properties properties = new Properties();

    public KernelConfig() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("kernel.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            System.err.println("Failed to load kernel.properties, using defaults.");
        }
    }

    public int getMaxProcesses() {
        return Integer.parseInt(properties.getProperty("kernel.maxProcesses", "4096"));
    }

    public int getDefaultPriority() {
        return Integer.parseInt(properties.getProperty("kernel.defaultPriority", "5"));
    }

    public long getMaxMemory() {
        return Long.parseLong(properties.getProperty("kernel.maxMemory", "1073741824"));
    }

    public String getSchedulerType() {
        return properties.getProperty("kernel.scheduler", "ROUND_ROBIN");
    }

    public String getLogLevel() {
        return properties.getProperty("kernel.logLevel", "INFO");
    }
}
