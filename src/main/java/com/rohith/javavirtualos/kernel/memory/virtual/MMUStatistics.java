package com.rohith.javavirtualos.kernel.memory.virtual;

public class MMUStatistics {
    private long translations = 0;
    private long pageFaults = 0;
    private long minorPageFaults = 0;
    private long majorPageFaults = 0;
    private long tlbHits = 0;
    private long tlbMisses = 0;
    private long cowFaults = 0;
    private long swapIns = 0;
    private long swapOuts = 0;
    private long allocatedFrames = 0;
    private long hugePages = 0;
    
    public void recordTranslation() { translations++; }
    public void recordMinorPageFault() { pageFaults++; minorPageFaults++; }
    public void recordMajorPageFault() { pageFaults++; majorPageFaults++; }
    public void recordTlbHit() { tlbHits++; }
    public void recordTlbMiss() { tlbMisses++; }
    public void recordCowFault() { cowFaults++; }
    public void recordSwapIn() { swapIns++; }
    public void recordSwapOut() { swapOuts++; }
    public void recordFrameAllocated() { allocatedFrames++; }
    public void recordFrameFreed() { allocatedFrames--; }
    public void recordHugePageAllocated() { hugePages++; }
    public void recordHugePageFreed() { hugePages--; }
    
    public long getTranslations() { return translations; }
    public long getPageFaults() { return pageFaults; }
    public long getMinorPageFaults() { return minorPageFaults; }
    public long getMajorPageFaults() { return majorPageFaults; }
    public long getTlbHits() { return tlbHits; }
    public long getTlbMisses() { return tlbMisses; }
    public long getCowFaults() { return cowFaults; }
    public long getSwapIns() { return swapIns; }
    public long getSwapOuts() { return swapOuts; }
    public long getAllocatedFrames() { return allocatedFrames; }
    public long getHugePages() { return hugePages; }
    
    public double getTlbHitRatio() {
        long total = tlbHits + tlbMisses;
        return total == 0 ? 0 : ((double) tlbHits / total) * 100.0;
    }
}
