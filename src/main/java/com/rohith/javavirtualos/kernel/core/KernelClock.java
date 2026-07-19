package com.rohith.javavirtualos.kernel.core;

import java.time.Instant;

public interface KernelClock {
    Instant now();
}
