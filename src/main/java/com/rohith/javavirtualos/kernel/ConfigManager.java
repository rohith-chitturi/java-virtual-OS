package com.rohith.javavirtualos.kernel;

import java.util.Properties;

/**
 * Manages configuration loaded from system.properties
 */
public class ConfigManager {

    private final Properties properties;

    public ConfigManager() {
        this.properties = new Properties();
    }

    public void load(String resourcePath) {
        try {
            var stream = getClass().getResourceAsStream(resourcePath);
            if (stream != null) {
                properties.load(stream);
            } else {
                System.err.println("WARN: Could not find config: " + resourcePath);
            }
        } catch (Exception e) {
            System.err.println("WARN: Error loading config: " + e.getMessage());
        }
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
