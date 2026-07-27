package com.rohith.javavirtualos.kernel.core;

import java.time.Instant;

public class SystemClock implements KernelClock {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
