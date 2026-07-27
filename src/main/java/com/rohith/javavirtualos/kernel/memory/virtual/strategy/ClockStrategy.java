package com.rohith.javavirtualos.kernel.memory.virtual.strategy;

import com.rohith.javavirtualos.kernel.memory.virtual.Frame;
import com.rohith.javavirtualos.kernel.memory.virtual.FrameTable;
import java.util.ArrayList;
import java.util.List;

public class ClockStrategy implements PageReplacementStrategy {
    private final List<Frame> frames = new ArrayList<>();
    private int pointer = 0;

    @Override
    public Frame chooseVictim(FrameTable frameTable) {
        if (frames.isEmpty()) throw new IllegalStateException("No frames available for replacement");

        while (true) {
            Frame current = frames.get(pointer);
            FrameTable.FrameTableEntry entry = frameTable.getEntries().stream()
                .filter(e -> e.getFrame().equals(current))
                .findFirst().orElseThrow();
            
            if (entry.isReferenced()) {
                entry.setReferenced(false); // Give second chance
            } else {
                Frame victim = current;
                frames.remove(pointer);
                if (pointer >= frames.size()) pointer = 0;
                return victim;
            }
            
            pointer++;
            if (pointer >= frames.size()) pointer = 0;
        }
    }

    @Override
    public void recordAccess(Frame frame) {
        if (!frames.contains(frame)) {
            frames.add(frame);
        }
    }

    @Override
    public String getName() { return "Clock"; }
}
