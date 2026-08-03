package com.rohith.javavirtualos.kernel.events.boot;

public class NetworkInitializedEvent extends BootEvent {
    @Override
    public String getMessage() {
        return "Network subsystem initialized.";
    }
}
