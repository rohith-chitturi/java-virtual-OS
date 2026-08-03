package com.rohith.javavirtualos.kernel.process.runtime;

import com.rohith.javavirtualos.kernel.process.manager.ProcessManager;
import com.rohith.javavirtualos.kernel.filesystem.jvfs.JVFSBlockDevice;

/**
 * Acts as the bridge between the Virtual Machine instances and the OS kernel.
 * Handles SYSCALL instructions.
 */
public class SystemCallInterface {
    
    private final ProcessManager processManager;
    private final JVFSBlockDevice blockDevice; // Used for I/O syscalls

    public SystemCallInterface(ProcessManager processManager, JVFSBlockDevice blockDevice) {
        this.processManager = processManager;
        this.blockDevice = blockDevice;
    }

    /**
     * Executes a system call.
     * @param context the execution context making the syscall
     * @param syscallId the ID of the syscall (e.g. 1 = EXIT, 2 = READ, 3 = WRITE)
     * @param arg1 generic argument 1
     * @param arg2 generic argument 2
     * @return the result of the syscall, often placed back into R0 by the VM.
     */
    public int handleSyscall(ExecutionContext context, int syscallId, int arg1, int arg2) {
        switch (syscallId) {
            case 1: // SYS_EXIT
                return handleExit(context, arg1);
            case 2: // SYS_PRINT (debug)
                System.out.println("[VM " + context.getProcessId() + " stdout] " + arg1);
                return 0;
            case 3: // SYS_SLEEP
                try {
                    Thread.sleep(arg1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return 0;
            // Additional syscalls (OPEN, READ, WRITE) can be added here
            default:
                throw new UnsupportedOperationException("Unknown syscall ID: " + syscallId);
        }
    }
    
    private int handleExit(ExecutionContext context, int exitCode) {
        // Here we'd ask the ProcessManager to terminate the process
        // For now, we return the exit code which the VM interprets as termination
        return exitCode;
    }
}
