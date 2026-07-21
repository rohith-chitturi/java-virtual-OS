package com.rohith.javavirtualos.kernel.memory.virtual.strategy;

import com.rohith.javavirtualos.kernel.memory.virtual.Frame;
import com.rohith.javavirtualos.kernel.memory.virtual.FrameTable;

public interface PageReplacementStrategy {
    Frame chooseVictim(FrameTable frameTable);
    void recordAccess(Frame frame);
    String getName();
}
