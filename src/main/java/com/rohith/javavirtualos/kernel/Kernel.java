package com.rohith.javavirtualos.kernel;

import com.rohith.javavirtualos.shell.Shell;
import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.services.DefaultFileSystemService;
import com.rohith.javavirtualos.kernel.process.ProcessManager;
import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.services.DefaultProcessService;

/**
 * The central coordinator for the virtual operating system.
 */
public class Kernel {
    
    private ConfigManager configManager;
    private SystemContext systemContext;
    private Shell shell;
    private UserManager userManager;
    private SecurityManager securityManager;
    private ProcessManager processManager;

    public void initialize() {
        // 1. Initialize Configuration
        this.configManager = new ConfigManager();
        this.configManager.load("/system.properties");
        
        // 2. Initialize System Context
        this.systemContext = new SystemContext(configManager);
        
        System.out.println("[ OK ] Initializing Memory Manager"); // Stub output
        
        // File System Initialization
        System.out.println("[ OK ] Mounting Virtual File System");
        FileSystemManager fsManager = new FileSystemManager();
        FileSystemService fsService = new DefaultFileSystemService(fsManager);

        System.out.println("[ OK ] Loading Command Registry");
        System.out.println("[ OK ] Starting Process Manager");
        this.processManager = new ProcessManager();
        ProcessService processService = new DefaultProcessService(processManager);

        System.out.println("[ OK ] Initializing Security Manager");
        this.securityManager = new SecurityManager();
        this.userManager = new UserManager();
        fsManager.setSecurityManager(securityManager); // We'll add this method
        
        // 3. Initialize Shell
        System.out.println("[ OK ] Starting Virtual Shell");
        this.shell = new Shell(systemContext, fsService, processService, userManager);
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
