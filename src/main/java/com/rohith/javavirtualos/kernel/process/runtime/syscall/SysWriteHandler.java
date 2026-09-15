package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.descriptor.Descriptor;
import com.rohith.javavirtualos.kernel.process.descriptor.StreamDescriptor;

import java.io.PrintStream;
import java.util.Optional;

import com.rohith.javavirtualos.kernel.process.descriptor.OpenFile;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.filesystem.FileSystemManager;

public class SysWriteHandler implements SystemCallHandler {
    private final FileSystemManager fsManager;

    public SysWriteHandler(FileSystemManager fsManager) {
        this.fsManager = fsManager;
    }

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
                OpenFile openFile = (OpenFile) desc;
                Inode inode = openFile.getFile();
                int cursor = openFile.getCursor();
                try {
                    fsManager.getFileStorage().write(inode, cursor, new byte[]{(byte)data});
                    openFile.setCursor(cursor + 1);
                    return SystemCallResult.success(1);
                } catch (Exception e) {
                    return SystemCallResult.error(-1);
                }
            }
        }
        return SystemCallResult.error(-1);
    }
}
