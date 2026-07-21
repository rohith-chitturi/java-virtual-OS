package com.rohith.javavirtualos.kernel.memory.virtual;

public class FrameStatistics {
    private final FrameTable frameTable;
    
    public FrameStatistics(FrameTable frameTable) {
        this.frameTable = frameTable;
    }
    
    public int getTotalFrames() { return frameTable.getTotalFrames(); }
    public int getFreeFrames() { return frameTable.getFreeFrames(); }
    public int getAllocatedFrames() { return frameTable.getAllocatedFrames(); }
    
    public double getUtilizationPercentage() {
        int total = getTotalFrames();
        return total == 0 ? 0 : ((double) getAllocatedFrames() / total) * 100.0;
    }
}
