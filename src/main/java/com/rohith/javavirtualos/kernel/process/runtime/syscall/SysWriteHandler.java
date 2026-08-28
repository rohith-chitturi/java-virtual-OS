package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.descriptor.Descriptor;
import com.rohith.javavirtualos.kernel.process.descriptor.StreamDescriptor;

import java.io.PrintStream;
import java.util.Optional;

public class SysWriteHandler implements SystemCallHandler {
    @Override
    public SystemCallResult handle(ProcessControlBlock pcb, SystemCallRequest request) {
        int fd = request.getArg1();
        int data = request.getArg2();
        
        Optional<Descriptor> descOpt = pcb.getFileDescriptorTable().get(fd);
        if (descOpt.isPresent() && descOpt.get() instanceof StreamDescriptor) {
            PrintStream out = ((StreamDescriptor) descOpt.get()).getPrintStream();
            if (out != null) {
                // If the data is meant to be a character (or string pointer, though not supported yet)
                // We'll just print the integer value for now based on VM requirements
                out.println(data);
                return SystemCallResult.success(1);
            }
        }
        return SystemCallResult.error(-1);
    }
}
