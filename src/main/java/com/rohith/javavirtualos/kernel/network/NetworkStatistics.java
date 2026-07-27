package com.rohith.javavirtualos.kernel.network;

import java.util.concurrent.atomic.AtomicLong;

public class NetworkStatistics {
    private final AtomicLong packetsSent = new AtomicLong(0);
    private final AtomicLong packetsReceived = new AtomicLong(0);
    private final AtomicLong bytesSent = new AtomicLong(0);
    private final AtomicLong bytesReceived = new AtomicLong(0);
    private final AtomicLong activeSockets = new AtomicLong(0);
    private final AtomicLong failedBinds = new AtomicLong(0);

    public void recordPacketSent(int bytes) {
        packetsSent.incrementAndGet();
        bytesSent.addAndGet(bytes);
    }

    public void recordPacketReceived(int bytes) {
        packetsReceived.incrementAndGet();
        bytesReceived.addAndGet(bytes);
    }

    public void recordSocketOpened() {
        activeSockets.incrementAndGet();
    }

    public void recordSocketClosed() {
        activeSockets.decrementAndGet();
    }

    public void recordFailedBind() {
        failedBinds.incrementAndGet();
    }

    public long getPacketsSent() { return packetsSent.get(); }
    public long getPacketsReceived() { return packetsReceived.get(); }
    public long getBytesSent() { return bytesSent.get(); }
    public long getBytesReceived() { return bytesReceived.get(); }
    public long getActiveSockets() { return activeSockets.get(); }
    public long getFailedBinds() { return failedBinds.get(); }
}
