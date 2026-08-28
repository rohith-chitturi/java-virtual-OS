package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;

/**
 * Interface for a system call handler.
 */
public interface SystemCallHandler {
    
    /**
     * Handles a specific system call.
     */
    SystemCallResult handle(ProcessControlBlock pcb, SystemCallRequest request);
}

