package com.rohith.javavirtualos.kernel.ipc;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessState;

public class WakeupManager {
    
    public void blockProcess(ProcessControlBlock pcb) {
        if (pcb != null && pcb.getState() != ProcessState.TERMINATED) {
            pcb.setState(ProcessState.BLOCKED);
        }
    }
    
    public void wakeupProcess(ProcessControlBlock pcb) {
        if (pcb != null && pcb.getState() == ProcessState.BLOCKED) {
            pcb.setState(ProcessState.READY);
        }
    }
}
