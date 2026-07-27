package com.rohith.javavirtualos.kernel.memory.virtual;

public class MMUStatistics {
    private long translations = 0;
    private long pageFaults = 0;
    private long minorPageFaults = 0;
    private long majorPageFaults = 0;
    private long tlbHits = 0;
    private long tlbMisses = 0;
    
    public void recordTranslation() { translations++; }
    public void recordMinorPageFault() { pageFaults++; minorPageFaults++; }
    public void recordMajorPageFault() { pageFaults++; majorPageFaults++; }
    public void recordTlbHit() { tlbHits++; }
    public void recordTlbMiss() { tlbMisses++; }
    
    public long getTranslations() { return translations; }
    public long getPageFaults() { return pageFaults; }
    public long getMinorPageFaults() { return minorPageFaults; }
    public long getMajorPageFaults() { return majorPageFaults; }
    public long getTlbHits() { return tlbHits; }
    public long getTlbMisses() { return tlbMisses; }
    
    public double getTlbHitRatio() {
        long total = tlbHits + tlbMisses;
        return total == 0 ? 0 : ((double) tlbHits / total) * 100.0;
    }
}
