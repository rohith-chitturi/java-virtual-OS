package com.rohith.javavirtualos.kernel.memory.virtual;

import com.rohith.javavirtualos.kernel.memory.MemoryMap;
import com.rohith.javavirtualos.kernel.memory.MemoryConstants;
import com.rohith.javavirtualos.kernel.memory.PhysicalAddress;
import com.rohith.javavirtualos.kernel.memory.MemoryBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FrameTable {
    private final List<FrameTableEntry> entries;
    private final MemoryMap physicalMap;

    public static class FrameTableEntry {
        private final Frame frame;
        private boolean isFree;
        private Page occupant;
        private boolean referenced;

        public FrameTableEntry(Frame frame) {
            this.frame = frame;
            this.isFree = true;
            this.occupant = null;
            this.referenced = false;
        }

        public Frame getFrame() { return frame; }
        public boolean isFree() { return isFree; }
        public void setFree(boolean free) { isFree = free; }
        public Page getOccupant() { return occupant; }
        public void setOccupant(Page occupant) { this.occupant = occupant; }
        public boolean isReferenced() { return referenced; }
        public void setReferenced(boolean referenced) { this.referenced = referenced; }
    }

    public FrameTable(MemoryMap physicalMap) {
        this.physicalMap = physicalMap;
        this.entries = new ArrayList<>();
        initializeFrames();
    }

    private void initializeFrames() {
        long frameNumber = 0;
        for (MemoryBlock block : physicalMap.getBlocks()) {
            if (block.getState() == com.rohith.javavirtualos.kernel.memory.MemoryState.FREE) {
                long start = block.getStart().getAddress();
                long size = block.getSize().toBytes();
                long frameSize = MemoryConstants.FRAME_SIZE.toBytes();
                for (long offset = 0; offset < size; offset += frameSize) {
                    Frame f = new Frame(frameNumber++, new PhysicalAddress(start + offset));
                    entries.add(new FrameTableEntry(f));
                }
            }
        }
    }

    public List<FrameTableEntry> getEntries() { return entries; }

    public Optional<FrameTableEntry> getFreeFrame() {
        return entries.stream().filter(FrameTableEntry::isFree).findFirst();
    }
    
    public int getTotalFrames() { return entries.size(); }
    public int getFreeFrames() { return (int) entries.stream().filter(FrameTableEntry::isFree).count(); }
    public int getAllocatedFrames() { return getTotalFrames() - getFreeFrames(); }
}
