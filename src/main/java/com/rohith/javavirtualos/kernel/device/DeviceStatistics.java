package com.rohith.javavirtualos.kernel.device;

import java.util.concurrent.atomic.AtomicLong;

public class DeviceStatistics {
    private final AtomicLong reads = new AtomicLong(0);
    private final AtomicLong writes = new AtomicLong(0);
    private final AtomicLong bytesRead = new AtomicLong(0);
    private final AtomicLong bytesWritten = new AtomicLong(0);
    private final AtomicLong initCount = new AtomicLong(0);
    private final AtomicLong errors = new AtomicLong(0);

    public void recordRead(int bytes) {
        reads.incrementAndGet();
        bytesRead.addAndGet(bytes);
    }

    public void recordWrite(int bytes) {
        writes.incrementAndGet();
        bytesWritten.addAndGet(bytes);
    }

    public void recordInit() { initCount.incrementAndGet(); }
    public void recordError() { errors.incrementAndGet(); }

    public long getReads() { return reads.get(); }
    public long getWrites() { return writes.get(); }
    public long getBytesRead() { return bytesRead.get(); }
    public long getBytesWritten() { return bytesWritten.get(); }
    public long getInitCount() { return initCount.get(); }
    public long getErrors() { return errors.get(); }
}
