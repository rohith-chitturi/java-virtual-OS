package com.rohith.javavirtualos.kernel.events.boot;

public class MemoryInitializedEvent extends BootEvent {
    private final long totalMemoryBytes;

    public MemoryInitializedEvent(long totalMemoryBytes) {
        this.totalMemoryBytes = totalMemoryBytes;
    }

    @Override
    public String getMessage() {
        return "Memory initialized. Total memory: " + totalMemoryBytes + " bytes.";
    }
}
