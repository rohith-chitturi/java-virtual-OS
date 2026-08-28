package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.descriptor.Descriptor;
import com.rohith.javavirtualos.kernel.process.descriptor.StreamDescriptor;

import java.io.InputStream;
import java.io.IOException;
import java.util.Optional;

public class SysReadHandler implements SystemCallHandler {
    @Override
    public SystemCallResult handle(ProcessControlBlock pcb, SystemCallRequest request) {
        int fd = request.getArg1();
        
        Optional<Descriptor> descOpt = pcb.getFileDescriptorTable().get(fd);
        if (descOpt.isPresent() && descOpt.get() instanceof StreamDescriptor) {
            InputStream in = ((StreamDescriptor) descOpt.get()).getInputStream();
            if (in != null) {
                try {
                    int val = in.read();
                    return SystemCallResult.success(val);
                } catch (IOException e) {
                    return SystemCallResult.error(-1);
                }
            }
        }
        return SystemCallResult.error(-1);
    }
}
