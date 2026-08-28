package com.rohith.javavirtualos.kernel.events.boot;

public class KernelReadyEvent extends BootEvent {
    @Override
    public String getMessage() {
        return "Kernel boot sequence complete. System is ready.";
    }
}
