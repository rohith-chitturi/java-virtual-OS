package com.rohith.javavirtualos.kernel.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiCoreProcessor implements Processor {
    private final List<CPU> cores;

    public MultiCoreProcessor(int coreCount) {
        if (coreCount <= 0) {
            throw new IllegalArgumentException("Core count must be positive");
        }
        
        List<CPU> mutableCores = new ArrayList<>(coreCount);
        for (int i = 0; i < coreCount; i++) {
            mutableCores.add(new CPU(i));
        }
        this.cores = Collections.unmodifiableList(mutableCores);
    }

    @Override
    public List<CPU> getCores() {
        return cores;
    }

    @Override
    public int getCoreCount() {
        return cores.size();
    }
}
