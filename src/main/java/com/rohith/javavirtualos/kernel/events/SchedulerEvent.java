package com.rohith.javavirtualos.kernel.events;

import com.rohith.javavirtualos.kernel.process.scheduler.ContextSwitch;

public class SchedulerEvent extends KernelEvent {
    private final ContextSwitch contextSwitch;

    public SchedulerEvent(ContextSwitch contextSwitch) {
        this.contextSwitch = contextSwitch;
    }

    public ContextSwitch getContextSwitch() { return contextSwitch; }

    @Override
    public String getMessage() {
        String oldName = (contextSwitch.getOldPcb() != null) ? contextSwitch.getOldPcb().getCommandName() : "IDLE";
        String newName = (contextSwitch.getNewPcb() != null) ? contextSwitch.getNewPcb().getCommandName() : "IDLE";
        return String.format("Tick=%d ContextSwitch: %s -> %s Reason=%s", 
                contextSwitch.getTick(), oldName, newName, contextSwitch.getReason());
    }
}
