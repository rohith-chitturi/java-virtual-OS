package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.kernel.process.descriptor.OpenFile;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SysCloseTest {

    private SysCloseHandler handler;
    private ProcessControlBlock pcb;

    @BeforeEach
    public void setup() {
        handler = new SysCloseHandler();
        pcb = new ProcessControlBlock(1, 1, 1, 0, "test", new User("root", "password"), null, null, null);
    }

    @Test
    public void testCloseValidFd() {
        FileNode node = new FileNode("test.txt", "root", null);
        OpenFile openFile = new OpenFile(node);
        int fd = pcb.getFileDescriptorTable().allocate(openFile);
        
        SystemCallRequest request = new SystemCallRequest(10, fd, 0);
        SystemCallResult result = handler.handle(pcb, request);
        
        assertFalse(result.isError());
        assertFalse(pcb.getFileDescriptorTable().get(fd).isPresent());
        assertFalse(openFile.isOpen());
    }

    @Test
    public void testCloseInvalidFd() {
        SystemCallRequest request = new SystemCallRequest(10, 999, 0);
        SystemCallResult result = handler.handle(pcb, request);
        
        assertTrue(result.isError());
    }
}
