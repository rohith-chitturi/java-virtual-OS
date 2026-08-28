package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.runtime.ExecutionResult;

public class SysSleepHandler implements SystemCallHandler {
    @Override
    public SystemCallResult handle(ProcessControlBlock pcb, SystemCallRequest request) {
        // Actual sleep is handled by returning SLEEP(duration) from VM to Dispatcher.
        // This handler just returns success.
        return SystemCallResult.success(0);
    }
}
