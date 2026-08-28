package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;

public class SysGetUidHandler implements SystemCallHandler {
    @Override
    public SystemCallResult handle(ProcessControlBlock pcb, SystemCallRequest request) {
        return SystemCallResult.success(pcb.getOwnerUid());
    }
}
