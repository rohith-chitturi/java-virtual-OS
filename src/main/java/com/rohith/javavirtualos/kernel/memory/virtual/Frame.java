package com.rohith.javavirtualos.kernel.memory.virtual;

import com.rohith.javavirtualos.kernel.memory.PhysicalAddress;

public class Frame {
    private final long frameNumber;
    private final PhysicalAddress startAddress;

    public Frame(long frameNumber, PhysicalAddress startAddress) {
        this.frameNumber = frameNumber;
        this.startAddress = startAddress;
    }

    public long getFrameNumber() { return frameNumber; }
    public PhysicalAddress getStartAddress() { return startAddress; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Frame frame = (Frame) o;
        return frameNumber == frame.frameNumber;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(frameNumber);
    }
    
    @Override
    public String toString() {
        return "Frame(" + frameNumber + " @ " + startAddress + ")";
    }
}
