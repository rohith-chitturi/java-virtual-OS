package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.kernel.process.descriptor.OpenFile;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;

public class SysOpenHandler implements SystemCallHandler {
    private final FileSystemManager fsManager;

    public SysOpenHandler(FileSystemManager fsManager) {
        this.fsManager = fsManager;
    }

    @Override
    public SystemCallResult handle(ProcessControlBlock pcb, SystemCallRequest request) {
        String path = request.getStringArg();
        int mode = request.getArg1(); // 0 = READ, 1 = WRITE, 2 = READ_WRITE
        
        if (path == null || path.isEmpty()) {
            return SystemCallResult.error(-1);
        }
        
        try {
            // Resolve path from root
            Inode node = fsManager.resolvePath(path, fsManager.getRoot());
            
            if (node == null) {
                if (mode == 1 || mode == 2) {
                    // Create if missing on write mode
                    // Find parent directory
                    int lastSlash = path.lastIndexOf('/');
                    String parentPath = lastSlash > 0 ? path.substring(0, lastSlash) : "/";
                    String fileName = path.substring(lastSlash + 1);
                    
                    com.rohith.javavirtualos.filesystem.model.DirectoryNode parent = fsManager.resolveDirectory(parentPath, fsManager.getRoot());
                    fsManager.createFile(fileName, parent, pcb.getOwner());
                    node = fsManager.resolvePath(path, fsManager.getRoot());
                } else {
                    return SystemCallResult.error(-1); // File not found
                }
            }
            
            if (mode == 0 || mode == 2) {
                fsManager.validateReadAccess(node, pcb.getOwner());
            }
            if (mode == 1 || mode == 2) {
                fsManager.validateWriteAccess(node, pcb.getOwner());
            }
            
            OpenFile openFile = new OpenFile(node, fsManager.getLifecycleManager());
            fsManager.getLifecycleManager().incrementOpenReference(node);
            int fd = pcb.getFileDescriptorTable().allocate(openFile);
            return SystemCallResult.success(fd);
            
        } catch (Exception e) {
            return SystemCallResult.error(-1);
        }
    }
}
