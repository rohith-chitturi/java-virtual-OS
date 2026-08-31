package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;

public class SysCloseHandler implements SystemCallHandler {
    
    @Override
    public SystemCallResult handle(ProcessControlBlock pcb, SystemCallRequest request) {
        int fd = request.getArg1();
        
        boolean closed = pcb.getFileDescriptorTable().close(fd);
        if (closed) {
            return SystemCallResult.success(0);
        }
        return SystemCallResult.error(-1);
    }
}
