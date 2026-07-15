package com.rohith.javavirtualos.kernel;

import com.rohith.javavirtualos.shell.Shell;

/**
 * The central coordinator for the virtual operating system.
 */
public class Kernel {
    
    private ConfigManager configManager;
    private SystemContext systemContext;
    private Shell shell;

    public void initialize() {
        // 1. Initialize Configuration
        this.configManager = new ConfigManager();
        this.configManager.load("/system.properties");
        
        // 2. Initialize System Context
        this.systemContext = new SystemContext(configManager);
        
        // Future phases will initialize MemoryManager, FileSystemManager, etc.
        System.out.println("[ OK ] Initializing Memory Manager"); // Stub output
        System.out.println("[ OK ] Mounting Virtual File System"); // Stub output
        System.out.println("[ OK ] Loading Command Registry");
        System.out.println("[ OK ] Starting Process Manager"); // Stub output
        System.out.println("[ OK ] Initializing Security Manager"); // Stub output
        
        // 3. Initialize Shell
        System.out.println("[ OK ] Starting Virtual Shell");
        this.shell = new Shell(systemContext);
    }

    public void startShell() {
        if (shell != null) {
            shell.start();
        } else {
            System.err.println("PANIC: Shell is not initialized!");
            System.exit(1);
        }
    }

    public SystemContext getSystemContext() {
        return systemContext;
    }
}
