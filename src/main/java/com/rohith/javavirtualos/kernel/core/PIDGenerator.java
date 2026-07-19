package com.rohith.javavirtualos.kernel.core;

import java.util.concurrent.atomic.AtomicInteger;

public class PIDGenerator implements IdGenerator<Integer> {
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public Integer generateId() {
        return nextId.getAndIncrement();
    }
}
