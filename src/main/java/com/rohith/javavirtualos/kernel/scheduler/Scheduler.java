package com.rohith.javavirtualos.kernel.scheduler;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;

public interface Scheduler {
    void addProcess(ProcessControlBlock pcb);
    void removeProcess(ProcessControlBlock pcb);
    ProcessControlBlock nextProcess();
}
