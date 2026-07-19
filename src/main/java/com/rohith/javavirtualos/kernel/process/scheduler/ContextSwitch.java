package com.rohith.javavirtualos.kernel.process.scheduler;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;

public class ContextSwitch {
    public enum Reason {
        QUANTUM_EXPIRED,
        PROCESS_FINISHED,
        HIGHER_PRIORITY,
        PROCESS_BLOCKED,
        INITIAL_DISPATCH
    }

    private final ProcessControlBlock oldPcb;
    private final ProcessControlBlock newPcb;
    private final long tick;
    private final Reason reason;

    public ContextSwitch(ProcessControlBlock oldPcb, ProcessControlBlock newPcb, long tick, Reason reason) {
        this.oldPcb = oldPcb;
        this.newPcb = newPcb;
        this.tick = tick;
        this.reason = reason;
    }

    public ProcessControlBlock getOldPcb() { return oldPcb; }
    public ProcessControlBlock getNewPcb() { return newPcb; }
    public long getTick() { return tick; }
    public Reason getReason() { return reason; }
}
