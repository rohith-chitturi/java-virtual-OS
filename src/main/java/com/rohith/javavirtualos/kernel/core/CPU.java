package com.rohith.javavirtualos.kernel.core;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;

public class CPU {
    private final int coreId;
    private CPUState state;
    private ProcessControlBlock currentProcess;

    public CPU(int coreId) {
        this.coreId = coreId;
        this.state = CPUState.IDLE;
        this.currentProcess = null;
    }

    public int getCoreId() { return coreId; }
    
    public CPUState getState() { return state; }
    public void setState(CPUState state) { this.state = state; }
    
    public ProcessControlBlock getCurrentProcess() { return currentProcess; }
    public void setCurrentProcess(ProcessControlBlock process) { this.currentProcess = process; }
}
