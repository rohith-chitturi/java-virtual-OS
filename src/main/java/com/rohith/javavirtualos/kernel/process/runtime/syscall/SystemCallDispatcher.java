package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.manager.ProcessManager;
import com.rohith.javavirtualos.filesystem.FileSystemManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Dispatches system calls to registered handlers.
 */
public class SystemCallDispatcher {
    
    public static final int SYS_EXIT = 1;
    public static final int SYS_PRINT = 2; // Kept for PRINT instruction
    public static final int SYS_SLEEP = 3;
    public static final int SYS_OPEN = 4;
    public static final int SYS_READ = 5;
    public static final int SYS_WRITE = 6;
    public static final int SYS_YIELD = 7;
    public static final int SYS_GETPID = 8;
    public static final int SYS_GETUID = 9;

    private final Map<Integer, SystemCallHandler> handlers = new HashMap<>();

    public SystemCallDispatcher(ProcessManager processManager, FileSystemManager fsManager) {
        // Core handlers are expected to be injected or registered via registerHandler.
        // For testing/mocking, some dummy logic can be kept if no real handler exists.
    }

    public void registerHandler(int syscallId, SystemCallHandler handler) {
        handlers.put(syscallId, handler);
    }

    public SystemCallResult dispatch(ProcessControlBlock pcb, SystemCallRequest request) {
        SystemCallHandler handler = handlers.get(request.getSyscallId());
        if (handler != null) {
            return handler.handle(pcb, request);
        }
        return SystemCallResult.error(1); // Unsupported syscall
    }
}

