package com.rohith.javavirtualos.kernel.events;

import java.time.Instant;

public abstract class KernelEvent {
    private final Instant timestamp;

    public KernelEvent() {
        this.timestamp = Instant.now();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public abstract String getMessage();
}
