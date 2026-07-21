package com.rohith.javavirtualos.kernel.memory.virtual.strategy;

import com.rohith.javavirtualos.kernel.memory.virtual.Frame;
import com.rohith.javavirtualos.kernel.memory.virtual.FrameTable;
import java.util.LinkedHashSet;
import java.util.Iterator;

public class LRUStrategy implements PageReplacementStrategy {
    private final LinkedHashSet<Frame> accessOrder = new LinkedHashSet<>();

    @Override
    public Frame chooseVictim(FrameTable frameTable) {
        Iterator<Frame> it = accessOrder.iterator();
        if (!it.hasNext()) {
            throw new IllegalStateException("No frames available for replacement");
        }
        Frame victim = it.next();
        it.remove();
        return victim;
    }

    @Override
    public void recordAccess(Frame frame) {
        accessOrder.remove(frame);
        accessOrder.add(frame);
    }

    @Override
    public String getName() { return "LRU"; }
}
