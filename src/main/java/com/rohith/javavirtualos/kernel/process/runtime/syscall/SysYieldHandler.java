package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.runtime.ExecutionResult;

public class SysYieldHandler implements SystemCallHandler {
    @Override
    public SystemCallResult handle(ProcessControlBlock pcb, SystemCallRequest request) {
        // The VM step() will return ExecutionResult.YIELD immediately after receiving this
        // So the dispatcher handles it.
        return SystemCallResult.success(0);
    }
}
