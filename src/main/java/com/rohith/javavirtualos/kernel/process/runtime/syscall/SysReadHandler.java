package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.descriptor.Descriptor;
import com.rohith.javavirtualos.kernel.process.descriptor.StreamDescriptor;

import java.io.InputStream;
import java.io.IOException;
import java.util.Optional;

import com.rohith.javavirtualos.kernel.process.descriptor.OpenFile;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.filesystem.FileSystemManager;

public class SysReadHandler implements SystemCallHandler {
    private final FileSystemManager fsManager;

    public SysReadHandler(FileSystemManager fsManager) {
        this.fsManager = fsManager;
    }

    @Override
    public SystemCallResult handle(ProcessControlBlock pcb, SystemCallRequest request) {
        int fd = request.getArg1();
        
        Optional<Descriptor> descOpt = pcb.getFileDescriptorTable().get(fd);
        if (descOpt.isPresent()) {
            Descriptor desc = descOpt.get();
            if (desc instanceof StreamDescriptor) {
                InputStream in = ((StreamDescriptor) desc).getInputStream();
                if (in != null) {
                    try {
                        int val = in.read();
                        return SystemCallResult.success(val);
                    } catch (IOException e) {
                        return SystemCallResult.error(-1);
                    }
                }
            } else if (desc instanceof OpenFile) {
                OpenFile openFile = (OpenFile) desc;
                Inode inode = openFile.getFile();
                int cursor = openFile.getCursor();
                try {
                    byte[] data = fsManager.getFileStorage().read(inode, cursor, 1);
                    if (data != null && data.length > 0) {
                        openFile.setCursor(cursor + 1);
                        return SystemCallResult.success(data[0]);
                    }
                } catch (Exception e) {
                    return SystemCallResult.error(-1);
                }
            }
        }
        return SystemCallResult.error(-1);
    }
}
