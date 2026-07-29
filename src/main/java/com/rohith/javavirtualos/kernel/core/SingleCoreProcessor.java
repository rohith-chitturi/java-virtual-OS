package com.rohith.javavirtualos.kernel.core;

import java.util.Collections;
import java.util.List;

public class SingleCoreProcessor implements Processor {
    private final CPU core;
    private final List<CPU> cores;

    public SingleCoreProcessor() {
        this.core = new CPU(0);
        this.cores = Collections.singletonList(core);
    }

    @Override
    public List<CPU> getCores() {
        return cores;
    }

    @Override
    public int getCoreCount() {
        return 1;
    }
}
