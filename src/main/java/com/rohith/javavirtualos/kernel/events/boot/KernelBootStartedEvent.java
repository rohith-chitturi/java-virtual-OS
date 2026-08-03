package com.rohith.javavirtualos.kernel.events.boot;

public class KernelBootStartedEvent extends BootEvent {
    @Override
    public String getMessage() {
        return "Kernel boot sequence started.";
    }
}
