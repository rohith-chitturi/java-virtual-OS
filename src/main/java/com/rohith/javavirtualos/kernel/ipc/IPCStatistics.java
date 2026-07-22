package com.rohith.javavirtualos.kernel.ipc;

public class IPCStatistics {
    private long signalsSent;
    private long signalsDelivered;
    private long pipesCreated;
    private long pipeBytesTransferred;
    private long sharedMemorySegments;
    private long sharedMemoryMappings;
    private long blockedReads;
    private long blockedWrites;

    public void recordSignalSent() { signalsSent++; }
    public void recordSignalDelivered() { signalsDelivered++; }
    public void recordPipeCreated() { pipesCreated++; }
    public void recordPipeBytesTransferred(long bytes) { pipeBytesTransferred += bytes; }
    public void recordSharedMemorySegmentCreated() { sharedMemorySegments++; }
    public void recordSharedMemoryMapped() { sharedMemoryMappings++; }
    public void recordBlockedRead() { blockedReads++; }
    public void recordBlockedWrite() { blockedWrites++; }

    public long getSignalsSent() { return signalsSent; }
    public long getSignalsDelivered() { return signalsDelivered; }
    public long getPipesCreated() { return pipesCreated; }
    public long getPipeBytesTransferred() { return pipeBytesTransferred; }
    public long getSharedMemorySegments() { return sharedMemorySegments; }
    public long getSharedMemoryMappings() { return sharedMemoryMappings; }
    public long getBlockedReads() { return blockedReads; }
    public long getBlockedWrites() { return blockedWrites; }
}
