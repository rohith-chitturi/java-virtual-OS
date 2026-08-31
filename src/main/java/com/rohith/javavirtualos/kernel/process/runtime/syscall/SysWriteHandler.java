package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.descriptor.Descriptor;
import com.rohith.javavirtualos.kernel.process.descriptor.StreamDescriptor;

import java.io.PrintStream;
import java.util.Optional;

import com.rohith.javavirtualos.kernel.process.descriptor.OpenFile;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.model.Inode;

public class SysWriteHandler implements SystemCallHandler {
    @Override
    public SystemCallResult handle(ProcessControlBlock pcb, SystemCallRequest request) {
        int fd = request.getArg1();
        int data = request.getArg2();
        
        Optional<Descriptor> descOpt = pcb.getFileDescriptorTable().get(fd);
        if (descOpt.isPresent()) {
            Descriptor desc = descOpt.get();
            if (desc instanceof StreamDescriptor) {
                PrintStream out = ((StreamDescriptor) desc).getPrintStream();
                if (out != null) {
                    out.println((char)data);
                    return SystemCallResult.success(1);
                }
            } else if (desc instanceof OpenFile) {
                Inode inode = ((OpenFile) desc).getFile();
                if (inode instanceof FileNode) {
                    ((FileNode) inode).appendContent(String.valueOf((char)data));
                    return SystemCallResult.success(1);
                }
            }
        }
        return SystemCallResult.error(-1);
    }
}
