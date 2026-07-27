package com.rohith.javavirtualos.kernel.memory.virtual.strategy;

import com.rohith.javavirtualos.kernel.memory.virtual.Frame;
import com.rohith.javavirtualos.kernel.memory.virtual.FrameTable;
import java.util.LinkedList;
import java.util.Queue;

public class FIFOStrategy implements PageReplacementStrategy {
    private final Queue<Frame> queue = new LinkedList<>();

    @Override
    public Frame chooseVictim(FrameTable frameTable) {
        Frame victim = queue.poll();
        if (victim == null) {
            throw new IllegalStateException("No frames available for replacement");
        }
        return victim;
    }

    @Override
    public void recordAccess(Frame frame) {
        if (!queue.contains(frame)) {
            queue.add(frame);
        }
    }

    @Override
    public String getName() { return "FIFO"; }
}
