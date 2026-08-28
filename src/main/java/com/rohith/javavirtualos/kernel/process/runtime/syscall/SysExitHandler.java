package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;

public class SysExitHandler implements SystemCallHandler {
    @Override
    public SystemCallResult handle(ProcessControlBlock pcb, SystemCallRequest request) {
        int exitCode = request.getArg1();
        // Exit is handled by returning ExecutionResult.EXIT to KernelDispatcher.
        // We just return the exit code here.
        return SystemCallResult.success(exitCode);
    }
}
