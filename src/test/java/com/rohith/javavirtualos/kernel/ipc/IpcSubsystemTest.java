package com.rohith.javavirtualos.kernel.ipc;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.state.ProcessState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IpcSubsystemTest {

    @Test
    void testPipeBlockingBehavior() {
        WakeupManager wakeup = new WakeupManager();
        Pipe pipe = new Pipe(1, "test-pipe", 5, wakeup);
        
        ProcessControlBlock reader = new ProcessControlBlock(100, 100, 100, 0, "reader", null, null, null, null);
        ProcessControlBlock writer = new ProcessControlBlock(101, 101, 101, 0, "writer", null, null, null, null);
        reader.setState(ProcessState.READY);
        writer.setState(ProcessState.READY);
        
        // 1. Empty pipe blocks reader
        Integer data = pipe.read(reader);
        assertNull(data);
        assertEquals(ProcessState.BLOCKED, reader.getState());
        
        // 2. Writer writes, wakes reader
        assertTrue(pipe.write((byte) 42, writer));
        assertEquals(ProcessState.READY, reader.getState());
        
        // 3. Fill pipe
        for (int i = 0; i < 4; i++) {
            assertTrue(pipe.write((byte) i, writer));
        }
        
        // 4. Full pipe blocks writer
        assertFalse(pipe.write((byte) 99, writer));
        assertEquals(ProcessState.BLOCKED, writer.getState());
        
        // 5. Reader reads, wakes writer
        assertEquals(42, pipe.read(reader));
        assertEquals(ProcessState.READY, writer.getState());
    }
    
    @Test
    void testSignalHandling() {
        ProcessControlBlock pcb = new ProcessControlBlock(200, 200, 200, 0, "target", null, null, null, null);
        
        assertFalse(pcb.hasPendingSignals());
        pcb.enqueueSignal(Signal.SIGTERM);
        assertTrue(pcb.hasPendingSignals());
        
        assertEquals(Signal.SIGTERM, pcb.dequeueSignal());
        assertFalse(pcb.hasPendingSignals());
    }
}
