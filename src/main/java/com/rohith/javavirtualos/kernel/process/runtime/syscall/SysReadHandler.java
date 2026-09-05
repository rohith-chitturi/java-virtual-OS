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
// We need state in OpenFile for cursor. Let's assume sequential reading.
// For now, we will just return the first character if it's not empty, or -1.
// A proper implementation would need a cursor in OpenFile. 
// We will add cursor to OpenFile next.

public class SysReadHandler implements SystemCallHandler {
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
                if (inode instanceof FileNode) {
                    String content = ((FileNode) inode).getContent();
                    int cursor = openFile.getCursor();
                    if (cursor < content.length()) {
                        char c = content.charAt(cursor);
                        openFile.setCursor(cursor + 1);
                        return SystemCallResult.success(c);
                    }
                }
            }
        }
        return SystemCallResult.error(-1);
    }
}
